package com.team766.hal;

public interface BasicMotorController {

	/**
	 * Common interface for getting the output power of a motor controller.
	 *
	 * @return The current set power. Value is between -1.0 and 1.0.
	 */
	double get();

	/**
	 * Common interface for setting the output power of a motor controller.
	 *
	 * @param power The power to set. Value should be between -1.0 and 1.0.
	 */
	void set(double power);

	void restoreFactoryDefault();
}