package com.team766.hal;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;

/**
 * Interface for speed controlling devices over CAN bus.
 */

public interface CANSpeedController extends SpeedController {

	public enum ControlMode {
		PercentOutput,
		Position,
		Velocity,
		Current,
		Follower,
		MotionProfile,
		MotionMagic,
		MotionProfileArc,
		Disabled,
	}

	/**
	 * Read the motor position from the sensor attached to the speed controller.
	 */
	double getSensorPosition();

	/**
	 * Read the motor velocity from the sensor attached to the speed controller.
	 */
	double getSensorVelocity();

	/**
	 * Sets the appropriate output on the speed controller, depending on the mode.
	 * @param mode The output mode to apply.
	 * In PercentOutput, the output is between -1.0 and 1.0, with 0.0 as stopped.
	 * In Current mode, output value is in amperes.
	 * In Velocity mode, output value is in position change / 100ms.
	 * In Position mode, output value is in encoder ticks or an analog value,
	 *   depending on the sensor.
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
	
	/**
	 * Sets the motors encoder value to the given position.
	 * 
	 * @param position The desired set position
	 */
	void setPosition(int position);

	void follow(CANSpeedController leader);

	void setNeutralMode(NeutralMode neutralMode);

	ErrorCode config_kP(int slotIdx, double value, int timeoutMs);

	ErrorCode config_kI(int slotIdx, double value, int timeoutMs);

	ErrorCode config_kD(int slotIdx, double value, int timeoutMs);

	ErrorCode config_kF(int slotIdx, double value, int timeoutMs);

	ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice);

	ErrorCode configNominalOutputForward(double PercentOutput);

	ErrorCode configNominalOutputReverse(double PercentOutput);

	ErrorCode configPeakOutputForward(double PercentOutput);

	ErrorCode configPeakOutputReverse(double PercentOutput);

	ErrorCode configMotionCruiseVelocity(int sensorUnitsPer100ms);

	ErrorCode configMotionAcceleration(int sensorunitsPer100msPerSec);

	void setSensorPhase(boolean PhaseSensor);

	ErrorCode configFactoryDefault();

	void configOpenLoopRamp(double secondsFromNeutralToFull, int timeoutMs);

	void configClosedLoopRamp(double secondsFromNeutralToFull, int timeoutMs);
}
