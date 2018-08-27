package com.team766.hal;

public interface JoystickReader {
	 /**
	   * Get the value of the axis.
	   *
	   * @param axis The axis to read, starting at 0.
	   * @return The value of the axis.
	   */
	  public double getRawAxis(final int axis);
	  
	  /**
	   * Get the button value (starting at button 1)
	   *
	   * The appropriate button is returned as a boolean value.
	   *
	   * @param button The button number to be read (starting at 1).
	   * @return The state of the button.
	   */
	  public boolean getRawButton(final int button);
	  
	  /**
	   * Get the value of the POV
	   * 
	   * @return the value of the POV
	   */
	  public int getPOV();
	  
	  /**
	   * Get the state of the trigger
	   * 
	   * @return the state of the joystick trigger
	   */
	  public boolean getTrigger();
	  
	  /**
	   * Finds whether the trigger was pressed since the last check
	   * 
	   * @return whether the trigger was pressed since the last check
	   */
	  public boolean getTriggerPressed();
}
