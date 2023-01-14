package com.team766.hal;

public interface SolenoidController {

	/**
	 * Set the value of a solenoid.
	 *
	 * @param on
	 *            Turn the solenoid output off or on.
	 */
	public void set(boolean on);

	/**
	 * Read the current value of the solenoid.
	 *
	 * @return The current value of the solenoid.
	 */
	public boolean get();
}
