package com.team766.framework;

public interface LaunchedContext {
	/**
	 * Returns a string meant to uniquely identify this Context (e.g. for use in
	 * logging).
	 */
	public String getContextName();

	/**
	 * Returns true if this Context has finished running, false otherwise.
	 */
	public boolean isDone();

	/**
	 * Interrupt the running of this Context and force it to terminate.
	 */
	public void stop();
}