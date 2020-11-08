package com.team766.framework;

public interface LaunchedContext {
	public String getContextName();

	public boolean isDone();

	public void stop();
}