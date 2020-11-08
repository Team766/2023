package com.team766.framework;

import java.util.LinkedList;
import com.team766.logging.LoggerExceptionUtils;

public class Scheduler implements Runnable {
	private static Scheduler m_instance = new Scheduler();
	
	public static Scheduler getInstance() {
		return m_instance;
	}
	
	private LinkedList<Runnable> m_runnables = new LinkedList<Runnable>();
	
	public void add(Runnable runnable) {
		m_runnables.add(runnable);
	}
	
	public void cancel(Runnable runnable) {
		m_runnables.remove(runnable);
	}
	
	public void reset() {
		m_runnables.clear();
	}
	
	public LaunchedContext startAsync(RunnableWithContext func) {
		return new Context(func);
	}

	public LaunchedContext startAsync(Runnable func) {
		return new Context(func);
	}

	public void run() {
		for (Runnable runnable : new LinkedList<Runnable>(m_runnables)) {
			try {
				runnable.run();
			} catch (Exception ex) {
				ex.printStackTrace();
				LoggerExceptionUtils.logException(ex);
			}
		}
	}
}
