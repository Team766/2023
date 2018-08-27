package com.team766.hal;

/**
 * Interface for speed controlling devices.
 */

public interface SpeedController {

  /**
   * Common interface for getting the current set speed of a speed controller.
   *
   * @return The current set speed. Value is between -1.0 and 1.0.
   */
  double get();


  /**
   * Common interface for setting the speed of a speed controller.
   *
   * @param speed The speed to set. Value should be between -1.0 and 1.0.
   */
  void set(double speed);

  /**
   * Common interface for inverting direction of a speed controller.
   *
   * @param isInverted The state of inversion true is inverted.
   */
  void setInverted(boolean isInverted);

  /**
   * Common interface for returning if a speed controller is in the inverted
   * state or not.
   *$
   * @return isInverted The state of the inversion true is inverted.
   *
   */
  boolean getInverted();

  /**
   * Stops motor movement. Motor can be moved again by calling set without having
   * to re-enable the motor.
   */
  void stopMotor();
}
