package com.team766.framework;

public interface Command extends Runnable {
	public void start();
	public void stop();
}
