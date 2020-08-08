package com.team766.hal;

public interface DigitalInputReader {
	/**
	 * Get the value from a digital input channel. Retrieve the value of a
	 * single digital input channel from the FPGA.
	 *
	 * @return the status of the digital input
	 */
	public boolean get();
}
