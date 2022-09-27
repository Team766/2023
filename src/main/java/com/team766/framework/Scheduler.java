package com.team766.framework;

import java.util.LinkedList;
import java.util.stream.Collectors;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;

public class Scheduler implements Runnable {
	private static final Scheduler c_instance;
	private static final Thread c_monitor;

	static {
		c_instance = new Scheduler();
		c_monitor = new Thread(Scheduler::monitor);
		c_monitor.setDaemon(true);
		c_monitor.start();
	}
	
	public static Scheduler getInstance() {
		return c_instance;
	}

	private static void monitor() {
		int lastIterationCount = 0;
		Runnable lastRunning = null;
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			if (c_instance.m_running != null && 
				c_instance.m_iterationCount == lastIterationCount &&
				c_instance.m_running == lastRunning) {
				Logger.get(Category.FRAMEWORK).logRaw(
					Severity.ERROR,
					"The code has gotten stuck in "
						+ c_instance.m_running.toString()
						+ ". You probably have an unintended infinite loop or need to add a call to context.yield()");
				Logger.get(Category.FRAMEWORK).logRaw(
					Severity.INFO,
					Thread.getAllStackTraces()
						.entrySet()
						.stream()
						.map(e ->
							e.getKey().getName() + ":\n"
							+ StackTraceUtils.getStackTrace(e.getValue()))
						.collect(Collectors.joining("\n")));
			}

			lastIterationCount = c_instance.m_iterationCount;
			lastRunning = c_instance.m_running;
		}
	}
	
	private LinkedList<Runnable> m_runnables = new LinkedList<Runnable>();
	private int m_iterationCount = 0;
	private Runnable m_running = null;
	
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
		++m_iterationCount;
		for (Runnable runnable : new LinkedList<Runnable>(m_runnables)) {
			try {
				m_running = runnable;
				runnable.run();
			} catch (Exception ex) {
				ex.printStackTrace();
				LoggerExceptionUtils.logException(ex);
			} finally {
				m_running = null;
			}
		}
	}
}
