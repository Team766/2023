package com.team766.framework;

import java.util.function.BooleanSupplier;

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
	
	@Override
	protected void initialize() {
		m_threadSync = new Object();
		m_controlOwner = ControlOwner.MAIN_THREAD;
		m_done = false;
		m_thread = new Thread(this::threadFunction);
		m_thread.start();
	}
	
	private void waitForControl(ControlOwner thisOwner) {
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
	
	public final void run() {
		if (m_done) {
			stop();
			return;
		}
		
		if (m_blockingPredicate == null || m_blockingPredicate.getAsBoolean()) {
			transferControl(ControlOwner.MAIN_THREAD, ControlOwner.SUBROUTINE);
		}
	}
}
