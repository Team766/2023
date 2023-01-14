package com.team766.hal.wpilib;

public class SystemClock implements com.team766.hal.Clock {
	
	public static final SystemClock instance = new SystemClock();

	@Override
	public double getTime() {
		return System.currentTimeMillis() * 0.001;
	}

}
