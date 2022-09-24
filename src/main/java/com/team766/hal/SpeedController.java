package com.team766.hal;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;

/**
 * Interface for speed controlling devices.
 */

public interface SpeedController extends BasicSpeedController {

	public enum Type {
		VictorSP,
		VictorSPX,
		TalonSRX,
		SparkMax,
		TalonFX,
	}

	public enum ControlMode {
		PercentOutput,
		Position,
		Velocity,
		Current,
		Voltage,
		Follower,
		MotionProfile,
		MotionMagic,
		MotionProfileArc,
		Disabled,
	}

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
	 * Sets the appropriate output on the speed controller, depending on the mode.
	 * @param mode The output mode to apply.
	 * In PercentOutput, the output is between -1.0 and 1.0, with 0.0 as stopped.
	 * In Current mode, output value is in amperes.
	 * In Velocity mode, output value is in position change / 100ms.
	 * In Position mode, output value is in encoder ticks or an analog value,
	 * depending on the sensor.
	 * In Follower mode, the output value is the integer device ID of the talon to
	 * duplicate.
	 *
	 * @param value The setpoint value, as described above.
	 */
	void set(ControlMode mode, double value);

	/**
	 * Common interface for inverting direction of a speed controller.
	 *
	 * @param isInverted The state of inversion true is inverted.
	 */
	void setInverted(boolean isInverted);

	/**
	 * Common interface for returning if a speed controller is in the inverted
	 * state or not.
	 *
	 * @return isInverted The state of the inversion true is inverted.
	 */
	boolean getInverted();

	/**
	 * Stops motor movement. Motor can be moved again by calling set without having
	 * to re-enable the motor.
	 */
	void stopMotor();

	/**
	 * Read the motor position from the sensor attached to the speed controller.
	 */
	double getSensorPosition();

	/**
	 * Read the motor velocity from the sensor attached to the speed controller.
	 */
	double getSensorVelocity();

	/**
	 * Sets the motors encoder value to the given position.
	 * 
	 * @param position The desired set position
	 */
	void setSensorPosition(double position);

	void follow(SpeedController leader);

	void setNeutralMode(NeutralMode neutralMode);

	void setP(double value);

	void setI(double value);

	void setD(double value);

	void setFF(double value);

	void setSelectedFeedbackSensor(FeedbackDevice feedbackDevice);
	
	void setSensorInverted(boolean inverted);

	void setOutputRange(double minOutput, double maxOutput);

	void setCurrentLimit(double ampsLimit);

	void restoreFactoryDefault();

	void setOpenLoopRamp(double secondsFromNeutralToFull);

	void setClosedLoopRamp(double secondsFromNeutralToFull);
}
