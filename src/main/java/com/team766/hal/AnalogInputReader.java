package com.team766.hal;

public interface AnalogInputReader extends ControlInputReader {
	/**
	 * Get a scaled sample straight from this channel. The value is scaled to units of Volts using the
	 * calibrated scaling data from getLSBWeight() and getOffset().
	 *
	 * @return A scaled sample straight from this channel.
	 */
	public double getVoltage();

	// Implementation for ControlInputReader interface
	@Override
	public default double getPosition() {
		return getVoltage();
	}

	// Implementation for ControlInputReader interface
	@Override
	public default double getRate() {
		throw new UnsupportedOperationException("Analog input sensor does not have support for velocity");
	}
}
