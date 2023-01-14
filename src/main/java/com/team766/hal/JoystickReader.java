package com.team766.hal;

public interface JoystickReader {
	/**
	  * Get the value of the axis.
	  *
	  * @param axis The axis to read, starting at 0.
	  * @return The value of the axis.
	  */
	public double getAxis(int axis);
	  
	/**
	  * Get the button value (starting at button 1)
	  *
	  * The appropriate button is returned as a boolean value.
	  *
	  * @param button The button number to be read (starting at 1).
	  * @return The state of the button.
	  */
	public boolean getButton(int button);

	/**
	 * Whether the button was pressed since the last check. Button indexes begin at
	 * 1.
	 *
	 * @param button The button index, beginning at 1.
	 * @return Whether the button was pressed since the last check.
	 */
	public boolean getButtonPressed(int button);

	/**
	 * Whether the button was released since the last check. Button indexes begin at
	 * 1.
	 *
	 * @param button The button index, beginning at 1.
	 * @return Whether the button was released since the last check.
	 */
	public boolean getButtonReleased(int button);
	  
	/**
	  * Get the value of the POV
	  * 
	  * @return the value of the POV
	  */
	public int getPOV();
}
