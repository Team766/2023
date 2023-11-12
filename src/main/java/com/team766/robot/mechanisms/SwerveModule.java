package com.team766.robot.mechanisms;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import com.ctre.phoenix.sensors.CANCoder;
import com.team766.hal.MotorController;
import com.team766.hal.MotorController.ControlMode;
import com.team766.robot.constants.SwerveDriveConstants;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveModule {
	private final String modulePlacement;
	private final MotorController drive;
	private final MotorController steer;
	private final CANCoder encoder;
	private final double offset;

	/*
	 * Factor that converts between motor units and degrees
	 * Multiply to convert from degrees to motor units
	 * Divide to convert from motor units to degrees
	 */
	private static final double ENCODER_CONVERSION_FACTOR = (150.0 / 7.0) /*steering gear ratio*/ * (2048.0 / 360.0) /*encoder units to degrees*/;

	public SwerveModule(String modulePlacement, MotorController drive, MotorController steer, CANCoder encoder) {
		this.modulePlacement = modulePlacement;
		this.drive = drive;
		this.steer = steer;
		this.encoder = encoder;
		this.offset = computeEncoderOffset();

		// Current limit for motors to avoid breaker problems 
		drive.setCurrentLimit(SwerveDriveConstants.DRIVE_MOTOR_CURRENT_LIMIT);
		steer.setCurrentLimit(SwerveDriveConstants.STEER_MOTOR_CURRENT_LIMIT);
	}

	private double computeEncoderOffset() {
		return (steer.getSensorPosition() / ENCODER_CONVERSION_FACTOR) % 360 - encoder.getAbsolutePosition();
	}

	/**
	 * Controls just the steer for this module.
	 * Can be used to turn the wheels without moving
	 * @param vector the vector specifying the module's motion
	 */
	public void steer(Vector2D vector) {
		// Calculates the angle of the vector from -180° to 180°
		final double vectorTheta = Math.toDegrees(Math.atan2(vector.getY(), vector.getX()));

		// Add 360 * number of full rotations to vectorTheta, then add offset
		final double angleDegrees = vectorTheta + 360*(Math.round((steer.getSensorPosition()/ENCODER_CONVERSION_FACTOR - offset - vectorTheta)/360)) + offset;

		// Sets the degree of the steer wheel
		// Needs to multiply by encoderconversionfactor to translate into a unit the motor understands
		steer.set(ControlMode.Position, ENCODER_CONVERSION_FACTOR*angleDegrees);

		SmartDashboard.putNumber("[" + modulePlacement + "]" + "Angle", angleDegrees);
	}

	/**
	 * Controls both steer and power (based on the target vector) for this module.
	 * @param vector the vector specifying the module's motion
	 */
	public void driveAndSteer(Vector2D vector) {
		// apply the steer
		steer(vector);

		// sets the power to the magnitude of the vector
		// TODO: does this need to be clamped to a specific range?
		drive.set(vector.getNorm());
	}

	public void stopDrive() {
		drive.stopMotor();
	}
}