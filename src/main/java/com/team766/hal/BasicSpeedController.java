package com.team766.hal;

public interface BasicSpeedController {

	/**
	 * Common interface for getting the output power of a speed controller.
	 *
	 * @return The current set power. Value is between -1.0 and 1.0.
	 */
	double get();

	/**
	 * Common interface for setting the output power of a speed controller.
	 *
	 * @param power The power to set. Value should be between -1.0 and 1.0.
	 */
	void set(double power);

	void restoreFactoryDefault();
}