package com.team766.framework;

public final class StopWatch {
	private long startTime = 0;
	private long pauseTime = 0;
	
	public StopWatch() {
	}

	public void start() {
		startTime = System.currentTimeMillis();
		pauseTime = 0;
	}

	public void reset() {
		startTime = 0;
		pauseTime = 0;
	}

	public void pause() {
		pauseTime = System.currentTimeMillis();
	}

	public long elapsedTimeMillis() {
		if (startTime == 0) {
			// log error
			return 0;
		}

		if (pauseTime == 0) {
			return System.currentTimeMillis() - startTime;
		}

		return pauseTime - startTime;
	}
}
