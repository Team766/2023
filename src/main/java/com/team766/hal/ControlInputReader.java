package com.team766.hal;

public interface ControlInputReader {
	/**
	 * Get the current position of the mechanism read by the sensor.
	 */
	public double getPosition();

	/**
	 * Get the current rate of change of the position.
	 */
	public double getRate();
}
