package com.team766.framework;

import java.util.function.BooleanSupplier;

import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public abstract class Subroutine extends Command {
	private static enum ControlOwner {
		MAIN_THREAD,
		SUBROUTINE,
	}

	private Thread m_thread;
	private Object m_threadSync;
	private boolean m_done;
	private BooleanSupplier m_blockingPredicate;
	private ControlOwner m_controlOwner;
	private String m_previousWaitPoint;
	
	@Override
	protected final void initialize() {
		m_threadSync = new Object();
		m_previousWaitPoint = null;
		m_controlOwner = ControlOwner.MAIN_THREAD;
		m_done = false;
		m_thread = new Thread(this::threadFunction);
		m_thread.start();
	}

	private String getExecutionPoint() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (int i = 0; i < stack.length; ++i) {
			if (stack[i].getClassName() == this.getClass().getName()) {
				return stack[i].toString();
			}
		}
		return null;
	}
	
	private void waitForControl(ControlOwner thisOwner) {
		if (thisOwner == ControlOwner.SUBROUTINE) {
			String waitPointTrace = getExecutionPoint();
			if (waitPointTrace != null && !waitPointTrace.equals(m_previousWaitPoint)) {
				Logger.get(Category.COMMANDS).logRaw(Severity.INFO, getCommandName() + " is waiting at " + waitPointTrace);
				m_previousWaitPoint = waitPointTrace;
			}
		}
		synchronized (m_threadSync) {
			while (m_controlOwner != thisOwner && !m_done) {
				try {
					m_threadSync.wait();
				} catch (InterruptedException e) {
				}
			}
			m_controlOwner = thisOwner;
		}
	}
	
	private void transferControl(ControlOwner thisOwner, ControlOwner desiredOwner) {
		synchronized (m_threadSync) {
			if (m_controlOwner != thisOwner) {
				throw new IllegalStateException("Subroutine had control owner " + m_controlOwner + " but assumed control owner " + thisOwner);
			}
			m_controlOwner = desiredOwner;
			m_threadSync.notifyAll();
			waitForControl(thisOwner);
		}
	}
	
	protected abstract void subroutine();
	
	private void threadFunction() {
		waitForControl(ControlOwner.SUBROUTINE);
		try {
			subroutine();
		} finally {
			synchronized (m_threadSync) {
				m_done = true;
				m_threadSync.notifyAll();
			}
		}
	}
	
	protected void waitFor(BooleanSupplier predicate) {
		m_blockingPredicate = predicate;
		while (!predicate.getAsBoolean()) {
			transferControl(ControlOwner.SUBROUTINE, ControlOwner.MAIN_THREAD);
		}
	}

	protected void yield() {
		m_blockingPredicate = null;
		transferControl(ControlOwner.SUBROUTINE, ControlOwner.MAIN_THREAD);
	}

	protected void waitForSubroutine(Subroutine other) {
		waitFor(() -> other.isDone());
	}

	protected void waitForSeconds(double seconds) {
		double startTime = RobotProvider.instance.getClock().getTime();
		waitFor(() -> RobotProvider.instance.getClock().getTime() - startTime > seconds);
	}

	protected void callSubroutine(Subroutine other) {
		other.start();
		waitForSubroutine(other);
	}
	
	public final void run() {
		if (m_done) {
			stop();
			return;
		}
		if (m_blockingPredicate == null || m_blockingPredicate.getAsBoolean()) {
			transferControl(ControlOwner.MAIN_THREAD, ControlOwner.SUBROUTINE);
		}
	}

	public boolean isDone() {
		return m_done;
	}
}
