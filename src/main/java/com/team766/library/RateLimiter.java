package com.team766.library;

import com.team766.hal.RobotProvider;

public class RateLimiter {
	private final double periodSeconds;
	private double nextTime = 0;

	public RateLimiter(double periodSeconds) {
		this.periodSeconds = periodSeconds;
	}

	public boolean next() {
		final double now = RobotProvider.getTimeProvider().get();
		if (now > nextTime) {
			if (nextTime == 0) {
				// Lazy-initialize the first time, because TimeProvider in
				// simulation often isn't ready at construction time.
				nextTime = now;
			}
			nextTime += periodSeconds;
			return true;
		} else {
			return false;
		}
	}
}
