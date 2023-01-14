package com.team766.hal;

public interface EncoderReader extends ControlInputReader {

	/**
	 * Gets the current count. Returns the current count on the Encoder. This
	 * method compensates for the decoding type.
	 *
	 * @return Current count from the Encoder adjusted for the 1x, 2x, or 4x
	 *         scale factor.
	 */
	public int get();

	/**
	 * Reset the Encoder distance to zero. Resets the current count to zero on
	 * the encoder.
	 */
	public void reset();

	/**
	 * Determine if the encoder is stopped. Using the MaxPeriod value, a boolean
	 * is returned that is true if the encoder is considered stopped and false
	 * if it is still moving. A stopped encoder is one where the most recent
	 * pulse width exceeds the MaxPeriod.
	 *
	 * @return True if the encoder is considered stopped.
	 */
	public boolean getStopped();

	/**
	 * The last direction the encoder value changed.
	 *
	 * @return The last direction the encoder value changed.
	 */
	public boolean getDirection();

	/**
	 * Get the distance the robot has driven since the last reset.
	 *
	 * @return The distance driven since the last reset as scaled by the value
	 *         from setDistancePerPulse().
	 */
	public double getDistance();

	/**
	 * Get the current rate of the encoder. Units are distance per second as
	 * scaled by the value from setDistancePerPulse().
	 *
	 * @return The current rate of the encoder.
	 */
	public double getRate();

	/**
	 * Set the distance per pulse for this encoder. This sets the multiplier
	 * used to determine the distance driven based on the count value from the
	 * encoder. Do not include the decoding type in this scale. The library
	 * already compensates for the decoding type. Set this value based on the
	 * encoder's rated Pulses per Revolution and factor in gearing reductions
	 * following the encoder shaft. This distance can be in any units you like,
	 * linear or angular.
	 *
	 * @param distancePerPulse
	 *            The scale factor that will be used to convert pulses to useful
	 *            units.
	 */
	public void setDistancePerPulse(double distancePerPulse);

	// Implementation for ControlInputReader interface
	@Override
	public default double getPosition() {
		return getDistance();
	}
}
