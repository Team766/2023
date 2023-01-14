package com.team766.hal;

public interface PositionReader {
	/**
	 * Return the position of the robot along the global X axis.
	 *
	 * @return the current position coordinate in meters
	 */
	public double getX();

	/**
	 * Return the position of the robot along the global Y axis.
	 *
	 * @return the current position coordinate in meters
	 */
	public double getY();

	/**
	 * Return the angle that the robot is currently facing.
	 *
	 * @return the current heading angle in degrees
	 */
	public double getHeading();
}