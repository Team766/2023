package com.team766.framework;

import java.util.HashSet;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;

public final class Context implements Runnable, LaunchedContext {
	private static enum ControlOwner {
		MAIN_THREAD,
		SUBROUTINE,
	}
	private static enum State {
		RUNNING,
		CANCELED,
		DONE,
	}

	private static Context c_currentContext = null;

	static Context currentContext() {
		return c_currentContext;
	}

	private RunnableWithContext m_func;
	private Context m_parentContext;
	private Thread m_thread;
	private Object m_threadSync;
	private State m_state;
	private BooleanSupplier m_blockingPredicate;
	private ControlOwner m_controlOwner;
	private String m_previousWaitPoint;
	private HashSet<Mechanism> m_ownedMechanisms = new HashSet<Mechanism>();
	
	private Context(RunnableWithContext func, Context parentContext) {
		m_func = func;
		m_parentContext = parentContext;
		Logger.get(Category.PROCEDURES).logRaw(Severity.INFO, "Starting context " + getContextName() + " for " + func.toString());
		m_threadSync = new Object();
		m_previousWaitPoint = null;
		m_controlOwner = ControlOwner.MAIN_THREAD;
		m_state = State.RUNNING;
		m_thread = new Thread(this::threadFunction);
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
	
	public String getContextName() {
		return "Context/" + Integer.toHexString(hashCode()) + "/" + m_func.toString();
	}

	@Override
	public String toString() {
		return getContextName();
	}

	private String getExecutionPoint() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		int i = 0;
		for (; i < stack.length; ++i) {
			if (stack[i].getClassName() == this.getClass().getName()) {
				break;
			}
		}
		for (; i < stack.length; ++i) {
			if (stack[i].getClassName() != this.getClass().getName()) {
				return stack[i].toString();
			}
		}
		return null;
	}
	
	private void waitForControl(ControlOwner thisOwner) {
		if (thisOwner == ControlOwner.SUBROUTINE) {
			String waitPointTrace = getExecutionPoint();
			if (waitPointTrace != null && !waitPointTrace.equals(m_previousWaitPoint)) {
				Logger.get(Category.PROCEDURES).logRaw(Severity.DEBUG, getContextName() + " is waiting at " + waitPointTrace);
				m_previousWaitPoint = waitPointTrace;
			}
		}
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
	
	private void transferControl(ControlOwner thisOwner, ControlOwner desiredOwner) {
		synchronized (m_threadSync) {
			if (m_controlOwner != thisOwner) {
				throw new IllegalStateException("Subroutine had control owner " + m_controlOwner + " but assumed control owner " + thisOwner);
			}
			m_controlOwner = desiredOwner;
			if (m_controlOwner == ControlOwner.SUBROUTINE) {
				c_currentContext = this;
			} else {
				c_currentContext = null;
			}
			m_threadSync.notifyAll();
			waitForControl(thisOwner);
		}
	}
	
	private void threadFunction() {
		waitForControl(ControlOwner.SUBROUTINE);
		try {
			m_func.run(this);
			Logger.get(Category.PROCEDURES).logRaw(Severity.INFO, "Context " + getContextName() + " finished");
		} catch (ContextStoppedException ex) {
			Logger.get(Category.PROCEDURES).logRaw(Severity.WARNING, getContextName() + " was stopped");
		} catch (Exception ex) {
			ex.printStackTrace();
			LoggerExceptionUtils.logException(ex);
		} finally {
			synchronized (m_threadSync) {
				m_state = State.DONE;
				m_threadSync.notifyAll();
			}
			for (Mechanism m : m_ownedMechanisms) {
				// Don't use this.releaseOwnership here, because that would cause a
				// ConcurrentModificationException since we're iterating over m_ownedMechanisms
				m.releaseOwnership(this);
			}
			m_ownedMechanisms.clear();
		}
	}
	
	public void waitFor(BooleanSupplier predicate) {
		if (!predicate.getAsBoolean()) {
			m_blockingPredicate = predicate;
			transferControl(ControlOwner.SUBROUTINE, ControlOwner.MAIN_THREAD);
		}
	}
	
	public void waitFor(LaunchedContext otherContext) {
		waitFor(otherContext::isDone);
	}

	public void waitFor(LaunchedContext... otherContexts) {
		Stream<LaunchedContext> contextStream = Stream.of(otherContexts);
		waitFor(() -> contextStream.allMatch(LaunchedContext::isDone));
	}

	public void yield() {
		m_blockingPredicate = null;
		transferControl(ControlOwner.SUBROUTINE, ControlOwner.MAIN_THREAD);
	}

	public void waitForSeconds(double seconds) {
		double startTime = RobotProvider.instance.getClock().getTime();
		waitFor(() -> RobotProvider.instance.getClock().getTime() - startTime > seconds);
	}

	public LaunchedContext startAsync(RunnableWithContext func) {
		return new Context(func, this);
	}

	public LaunchedContext startAsync(Runnable func) {
		return new Context(func, this);
	}

	public void stop() {
		synchronized (m_threadSync) {
			if (m_state != State.DONE) {
				m_state = State.CANCELED;
			}
			if (m_controlOwner == ControlOwner.SUBROUTINE) {
				throw new ContextStoppedException();
			}
		}
	}
	
	public final void run() {
		if (m_state == State.DONE) {
			Scheduler.getInstance().cancel(this);
			return;
		}
		if (m_state == State.CANCELED || m_blockingPredicate == null || m_blockingPredicate.getAsBoolean()) {
			transferControl(ControlOwner.MAIN_THREAD, ControlOwner.SUBROUTINE);
		}
	}

	public boolean isDone() {
		return m_state == State.DONE;
	}
	
	public void takeOwnership(Mechanism mechanism) {
		mechanism.takeOwnership(this, m_parentContext);
		m_ownedMechanisms.add(mechanism);
	}

	public void releaseOwnership(Mechanism mechanism) {
		mechanism.releaseOwnership(this);
		m_ownedMechanisms.remove(mechanism);
	}
}