package com.team766.framework;

import java.lang.StackWalker.StackFrame;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;

/**
 * Context is the framework's representation of a single thread of execution.
 * 
 * We may want to have multiple procedures running at the same time on a robot.
 * For example, the robot could be raising an arm mechanism while also driving.
 * Each of those procedures would have a separate Context. Each of those
 * procedures may call other procedures directly; those procedures would share
 * the same Context. Each Context can only be running a single procedure at a
 * time. If a procedure wants to call multiple other procedures at the same
 * time, it has to create new Contexts for them (using the {@link #startAsync}
 * method).
 * 
 * Use the Context instance passed to your procedure whenever you want your
 * procedure to wait for something. For example, to have your procedure pause
 * for a certain amount of time, call context.waitForSeconds. Multiple Contexts
 * run at the same time using cooperative multitasking, which means procedures
 * have to explicitly indicate when another Context should be allowed to run.
 * Using Context's wait* methods will allow other Contexts to run while this one
 * is waiting. If your procedure will run for a while without needing to wait
 * (this often happens if your procedure has a while loop), then it should
 * periodically call context.yield() (for example, at the start of each
 * iteration of the while loop) to still allow other Contexts to run.
 * 
 * This cooperative multitasking paradigm is used by the framework to ensure
 * that only one Context is actually running at a time, which allows us to avoid
 * needing to deal with concurrency issues like data race conditions. Even
 * though only one Context is running at once, it's still incredibly helpful to
 * express the code using this separate-threads-of-execution paradigm, as it
 * allows each procedure to be written in procedural style
 * (https://en.wikipedia.org/wiki/Procedural_programming "procedural languages
 * model execution of the program as a sequence of imperative commands"), rather
 * than as state machines or in continuation-passing style, which can be much
 * more complicated to reason about, especially for new programmers.
 * 
 * Currently, threads of execution are implemented using OS threads, but this
 * should be considered an implementation detail and may change in the future.
 * Even though the framework creates multiple OS threads, it uses Java's
 * monitors to implement a "baton passing" pattern in order to ensure that only
 * one of threads is actually running at once (the others will be sleeping,
 * waiting for the baton to be passed to them).
 */
public final class Context implements Runnable, LaunchedContext {
	/**
	 * Represents the baton-passing state (see class comments). Instead of
	 * passing a baton directly from one Context's thread to the next, each
	 * Context has its own baton that gets passed from the program's main thread
	 * to the Context's thread and back. While this is less efficient (double
	 * the number of OS context switches required), it makes the code simpler
	 * and more modular.
	 */
	private static enum ControlOwner {
		MAIN_THREAD,
		SUBROUTINE,
	}
	/**
	 * Indicates the lifetime state of this Context.
	 */
	private static enum State {
		/**
		 * The Context has been started (a Context is started immediately upon
		 * construction).
		 */
		RUNNING,
		/**
		 * stop() has been called on this Context (but it has not been allowed
		 * to respond to the stop request yet).
		 */
		CANCELED,
		/**
		 * The Context's execution has come to an end.
		 */
		DONE,
	}

	private static Context c_currentContext = null;

	/**
	 * Returns the currently-executing Context.
	 * 
	 * This is maintained for things like checking Mechanism ownership, but
	 * intentionally only has package-private visibility - code outside of the
	 * framework should ideally pass around references to the current context
	 * object rather than cheating with this static accessor.
	 */
	static Context currentContext() {
		return c_currentContext;
	}

	/**
	 * The top-level procedure being run by this Context.
	 */
	private final RunnableWithContext m_func;
	/**
	 * If this Context was created by another context using
	 * {@link #startAsync}, this will contain a reference to that originating
	 * Context.
	 */
	private final Context m_parentContext;
	/**
	 * The OS thread that this Context is executing on.
	 */
	private final Thread m_thread;
	/**
	 * Used to synchronize access to this Context's state variable.
	 */
	private final Object m_threadSync;
	/**
	 * This Context's lifetime state.
	 */
	private State m_state;
	/**
	 * If one of the wait* methods has been called on this Context, this
	 * contains the predicate which should be checked to determine whether
	 * the Context's execution should be resumed. This makes it more efficient
	 * to poll completion criteria without needing to context-switch between
	 * threads.
	 */
	private BooleanSupplier m_blockingPredicate;
	/**
	 * Set to SUBROUTINE when this Context is executing and MAIN_THREAD
	 * otherwise.
	 */
	private ControlOwner m_controlOwner;
	/**
	 * Contains the method name and line number at which this Context most
	 * recently yielded.
	 */
	private String m_previousWaitPoint;
	/**
	 * The mechanisms that have been claimed by this Context using
	 * takeOwnership. These will be automatically released when the Context
	 * finishes executing.
	 */
	private Set<Mechanism> m_ownedMechanisms = new HashSet<Mechanism>();
	
	/*
	 * Constructors are intentionally private or package-private. New contexts
	 * should be created with {@link Context#startAsync} or
	 * {@link Scheduler#startAsync}.
	 */

	private Context(RunnableWithContext func, Context parentContext) {
		m_func = func;
		m_parentContext = parentContext;
		Logger.get(Category.FRAMEWORK).logRaw(Severity.INFO, "Starting context " + getContextName() + " for " + func.toString());
		m_threadSync = new Object();
		m_previousWaitPoint = null;
		m_controlOwner = ControlOwner.MAIN_THREAD;
		m_state = State.RUNNING;
		m_thread = new Thread(this::threadFunction, getContextName());
		m_thread.start();
		Scheduler.getInstance().add(this);
	}
	Context(RunnableWithContext func) {
		this(func, null);
	}

	private Context(Runnable func, Context parentContext) {
		this((context) -> func.run());
	}
	Context(Runnable func) {
		this(func, null);
	}
	
	/**
	 * Returns a string meant to uniquely identify this Context (e.g. for use in
	 * logging).
	 */
	public String getContextName() {
		return "Context/" + Integer.toHexString(hashCode()) + "/" + m_func.toString();
	}

	@Override
	public String toString() {
		String repr = getContextName();
		if (currentContext() == this) {
			repr += " running";
		}
		repr += "\n";
		repr += StackTraceUtils.getStackTrace(m_thread);
		return repr;
	}

	/**
	 * Walks up the call stack until it reaches a frame that isn't from the
	 * Context class, then returns a string representation of that frame. This
	 * is used to generate a concise string representation of from where the
	 * user called into framework code.
	 */
	private String getExecutionPoint() {
		StackWalker walker = StackWalker.getInstance();
		return walker
				.walk(s -> s.dropWhile(f -> f.getClassName() != Context.this.getClass().getName())
						.filter(f -> f.getClassName() != Context.this.getClass().getName())
						.findFirst()
						.map(StackFrame::toString)
						.orElse(null));
	}
	
	/**
	 * Wait until the baton (see the class comments) has been passed to this
	 * thread.
	 * 
	 * @param thisOwner the thread from which this function is being called
	 *     (and thus the baton-passing state that should be waited for)
	 * @throws ContextStoppedException if stop() is called on this Context while
	 *     waiting.
	 */
	private void waitForControl(ControlOwner thisOwner) {
		// If this is being called from the worker thread, log from where in the
		// user's code that the context is waiting. This is provided as a
		// convenience so the user can track the progress of execution through
		// their procedures.
		if (thisOwner == ControlOwner.SUBROUTINE) {
			String waitPointTrace = getExecutionPoint();
			if (waitPointTrace != null && !waitPointTrace.equals(m_previousWaitPoint)) {
				Logger.get(Category.FRAMEWORK).logRaw(Severity.DEBUG, getContextName() + " is waiting at " + waitPointTrace);
				m_previousWaitPoint = waitPointTrace;
			}
		}
		// Wait for the baton to be passed to us.
		synchronized (m_threadSync) {
			while (m_controlOwner != thisOwner && m_state != State.DONE) {
				try {
					m_threadSync.wait();
				} catch (InterruptedException e) {
				}
			}
			m_controlOwner = thisOwner;
			if (m_state != State.RUNNING && m_controlOwner == ControlOwner.SUBROUTINE) {
				throw new ContextStoppedException();
			}
		}
	}
	
	/**
	 * Pass the baton (see the class comments) to the other thread and then wait
	 * for it to be passed back.
	 * 
	 * @param thisOwner the thread from which this function is being called
	 *     (and thus the baton-passing state that should be waited for)
	 * @param desiredOwner the thread to which the baton should be passed
	 * @throws ContextStoppedException if stop() is called on this Context while
	 *     waiting.
	 */
	private void transferControl(ControlOwner thisOwner, ControlOwner desiredOwner) {
		synchronized (m_threadSync) {
			// Make sure we currently have the baton before trying to give it to
			// someone else.
			if (m_controlOwner != thisOwner) {
				throw new IllegalStateException("Subroutine had control owner " + m_controlOwner + " but assumed control owner " + thisOwner);
			}
			// Pass the baton.
			m_controlOwner = desiredOwner;
			if (m_controlOwner == ControlOwner.SUBROUTINE) {
				c_currentContext = this;
			} else {
				c_currentContext = null;
			}
			m_threadSync.notifyAll();
			// Wait for the baton to be passed back.
			waitForControl(thisOwner);
		}
	}
	
	/**
	 * This is the entry point for this Context's worker thread.
	 */
	private void threadFunction() {
		// OS threads run independently of one another, so we need to wait until
		// the baton is passed to us before we can start running the user's code
		waitForControl(ControlOwner.SUBROUTINE);
		try {
			// Call into the user's code.
			m_func.run(this);
			Logger.get(Category.FRAMEWORK).logRaw(Severity.INFO, "Context " + getContextName() + " finished");
		} catch (ContextStoppedException ex) {
			Logger.get(Category.FRAMEWORK).logRaw(Severity.WARNING, getContextName() + " was stopped");
		} catch (Exception ex) {
			ex.printStackTrace();
			LoggerExceptionUtils.logException(ex);
			Logger.get(Category.FRAMEWORK).logRaw(Severity.WARNING, "Context " + getContextName() + " died");
		} finally {
			for (Mechanism m : m_ownedMechanisms) {
				// Don't use this.releaseOwnership here, because that would cause a
				// ConcurrentModificationException since we're iterating over m_ownedMechanisms
				try {
					m.releaseOwnership(this);
				} catch (Exception ex) {
					LoggerExceptionUtils.logException(ex);
				}
			}
			synchronized (m_threadSync) {
				m_state = State.DONE;
				c_currentContext = null;
				m_threadSync.notifyAll();
			}
			m_ownedMechanisms.clear();
		}
	}
	
	/**
	 * Pauses the execution of this Context until the given predicate returns
	 * true. Yields to other Contexts in the meantime.
	 * 
	 * Note that the predicate will be evaluated repeatedly (possibly on a
	 * different thread) while the Context is paused to determine whether it
	 * should continue waiting.
	 */
	public void waitFor(BooleanSupplier predicate) {
		if (!predicate.getAsBoolean()) {
			m_blockingPredicate = predicate;
			transferControl(ControlOwner.SUBROUTINE, ControlOwner.MAIN_THREAD);
		}
	}
	
	/**
	 * Pauses the execution of this Context until the given LaunchedContext has
	 * finished running.
	 */
	public void waitFor(LaunchedContext otherContext) {
		waitFor(otherContext::isDone);
	}

	/**
	 * Pauses the execution of this Context until all of the given
	 * LaunchedContexts have finished running.
	 */
	public void waitFor(LaunchedContext... otherContexts) {
		waitFor(() -> Arrays.stream(otherContexts).allMatch(LaunchedContext::isDone));
	}

	/**
	 * Momentarily pause execution of this Context to allow other Contexts to
	 * execute. Execution of this Context will resume as soon as possible after
	 * the other Contexts have been given a chance to run.
	 * 
	 * Procedures should call this periodically if they wouldn't otherwise call
	 * one of the wait* methods for a while.
	 */
	public void yield() {
		m_blockingPredicate = null;
		transferControl(ControlOwner.SUBROUTINE, ControlOwner.MAIN_THREAD);
	}

	/**
	 * Pauses the execution of this Context for the given length of time.
	 */
	public void waitForSeconds(double seconds) {
		double startTime = RobotProvider.instance.getClock().getTime();
		waitFor(() -> RobotProvider.instance.getClock().getTime() - startTime > seconds);
	}

	/**
	 * Start running a new Context so the given procedure can run in parallel.
	 */
	public LaunchedContext startAsync(RunnableWithContext func) {
		return new Context(func, this);
	}

	/**
	 * Start running a new Context so the given procedure can run in parallel.
	 */
	public LaunchedContext startAsync(Runnable func) {
		return new Context(func, this);
	}

	/**
	 * Interrupt the running of this Context and force it to terminate.
	 * 
	 * A ContextStoppedException will be raised on this Context at the point
	 * where the Context most recently waited or yielded -- if this Context is
	 * currently executing, a ContextStoppedException will be raised
	 * immediately.
	 */
	@Override
	public void stop() {
		Logger.get(Category.FRAMEWORK).logRaw(Severity.INFO, "Stopping requested of " + getContextName());
		synchronized (m_threadSync) {
			if (m_state != State.DONE) {
				m_state = State.CANCELED;
			}
			if (m_controlOwner == ControlOwner.SUBROUTINE) {
				throw new ContextStoppedException();
			}
		}
	}
	
	/**
	 * Entry point for the Scheduler to execute this Context.
	 * 
	 * This should only be called from framework code; it is public only as an
	 * implementation detail.
	 */
	@Override
	public final void run() {
		if (m_state == State.DONE) {
			Scheduler.getInstance().cancel(this);
			return;
		}
		if (m_state == State.CANCELED || m_blockingPredicate == null || m_blockingPredicate.getAsBoolean()) {
			transferControl(ControlOwner.MAIN_THREAD, ControlOwner.SUBROUTINE);
		}
	}

	/**
	 * Returns true if this Context has finished running, false otherwise.
	 */
	public boolean isDone() {
		return m_state == State.DONE;
	}
	
	/**
	 * Take ownership of the given Mechanism with this Context.
	 * 
	 * Only one Context can own a Mechanism at one time. If any Context
	 * previously owned this Mechanism, it will be terminated.
	 * Ownership of this Mechanism can be released by calling releaseOwnership,
	 * or it will be automatically released when this Context finishes running.
	 * 
	 * @see Mechanism#takeOwnership(Context, Context)
	 */
	public void takeOwnership(Mechanism mechanism) {
		mechanism.takeOwnership(this, m_parentContext);
		m_ownedMechanisms.add(mechanism);
	}

	/**
	 * Release ownership of the given Mechanism.
	 * 
	 * It is an error to call this method with a Mechanism that was not
	 * previously passed to takeOwnership.
	 * 
	 * @see #takeOwnership(Mechanism)
	 * @see Mechanism#releaseOwnership(Context)
	 */
	public void releaseOwnership(Mechanism mechanism) {
		mechanism.releaseOwnership(this);
		m_ownedMechanisms.remove(mechanism);
	}
}