package com.team766.hal;

public interface AnalogInputReader {
	/**
	   * Get a scaled sample straight from this channel. The value is scaled to units of Volts using the
	   * calibrated scaling data from getLSBWeight() and getOffset().
	   *
	   * @return A scaled sample straight from this channel.
	   */
	  public double getVoltage();
}
