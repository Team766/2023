package com.team766.robot.mechanisms;

import com.ctre.phoenix.sensors.CANCoder;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController.ControlMode;
import com.team766.logging.Category;
import com.team766.robot.constants.SwerveDriveConstants;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Drive extends Mechanism {

	// Drive motors
	private MotorController m_DriveFR;
	private MotorController m_DriveFL;
	private MotorController m_DriveBR;
	private MotorController m_DriveBL;

	// Motors to turn each wheel in place
	private MotorController m_SteerFR;
	private MotorController m_SteerFL;
	private MotorController m_SteerBR;
	private MotorController m_SteerBL;

	// Absolute encoders (cancoders)
	private CANCoder e_FrontRight;
	private CANCoder e_FrontLeft;
	private CANCoder e_BackRight;
	private CANCoder e_BackLeft;

	/*
	 * Specific offsets to align each wheel
	 * These are assigned upon startup with setEncoderOffset()
	 */
	private double offsetFR;
	private double offsetFL;
	private double offsetBR;
	private double offsetBL;

	/*
	 * Factor that converts between motor units and degrees
	 * Multiply to convert from degrees to motor units
	 * Divide to convert from motor units to degrees
	 */
	private final double encoderConversionFactor = (150.0 / 7.0) /*steering gear ratio*/ * (2048.0 / 360.0) /*encoder units to degrees*/;

	public Drive() {
		
		loggerCategory = Category.DRIVE;
		// Initializations of motors

		// Initialize the drive motors
		m_DriveFR = RobotProvider.instance.getMotor("drive.DriveFrontRight");
		m_DriveFL = RobotProvider.instance.getMotor("drive.DriveFrontLeft");
		m_DriveBR = RobotProvider.instance.getMotor("drive.DriveBackRight");
		m_DriveBL = RobotProvider.instance.getMotor("drive.DriveBackLeft");

		// Initialize the steering motors
		m_SteerFR = RobotProvider.instance.getMotor("drive.SteerFrontRight");
		m_SteerFL = RobotProvider.instance.getMotor("drive.SteerFrontLeft");
		m_SteerBR = RobotProvider.instance.getMotor("drive.SteerBackRight");
		m_SteerBL = RobotProvider.instance.getMotor("drive.SteerBackLeft");

		

		// Initialize the encoders
		e_FrontRight = new CANCoder(22, "Swervavore");
		e_FrontLeft = new CANCoder(23, "Swervavore");
		e_BackRight = new CANCoder(21, "Swervavore");
		e_BackLeft = new CANCoder(24, "Swervavore");

		// Sets the offset value for each steering motor so that each is aligned
		setEncoderOffset();
	}

	/**
	 * Sets the motion for a specific module
	 * @param drive the drive motor of this module
	 * @param steer the steer motor of this module
	 * @param vector the vector specifying the module's motion
	 * @param offset the offset for this module
	 */
	public void setModule(MotorController drive, MotorController steer, Vector2D vector, double offset) {
		checkContextOwnership();

		// Calculates the angle of the vector from -180° to 180°
		final double vectorTheta = Math.toDegrees(Math.atan2(vector.getY(), vector.getX()));

		// Add 360 * number of full rotations to vectorTheta, then add offset
		final double angleDegrees = vectorTheta + 360*(Math.round((steer.getSensorPosition()/encoderConversionFactor - offset - vectorTheta)/360)) + offset;

		// Sets the degree of the steer wheel
		// Needs to multiply by encoderconversionfactor to translate into a language the motor understands
		steer.set(ControlMode.Position, encoderConversionFactor*angleDegrees);

		// Set the drive power to the vector's magnitude
		drive.set(vector.getNorm());

		SmartDashboard.putNumber("Angle", angleDegrees);
	}

	/** 
	 * Maps parameters to robot oriented swerve movement
	 * @param x the x value for the position joystick
	 * @param y the y value for the position joystick
	 * @param turn the turn value from the rotation joystick
	 */
	public void controlRobotOriented(double x, double y, double turn) {
		// Finds the vectors for turning and for translation of each module, and adds them
		// Applies this for each module
		setModule(m_DriveFL, m_SteerFL, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.fl_x, SwerveDriveConstants.fl_y).normalize()), offsetFL);
		setModule(m_DriveFR, m_SteerFR, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.fr_x, SwerveDriveConstants.fr_y).normalize()), offsetFR);
		setModule(m_DriveBR, m_SteerBR, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.br_x, SwerveDriveConstants.br_y).normalize()), offsetBR);
		setModule(m_DriveBL, m_SteerBL, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.bl_x, SwerveDriveConstants.bl_y).normalize()), offsetBL);
	}

	/**
	 * Uses controlRobotOriented() to control the robot relative to the field
	 * @param yaw the robot gyro's current yaw value
	 * @param x the x value for the position joystick
	 * @param y the y value for the position joystick
	 * @param turn the turn value from the rotation joystick
	 */
	public void controlFieldOriented(double yaw, double x, double y, double turn) {
		controlRobotOriented(Math.cos(yaw) * x - Math.sin(yaw) * y, Math.cos(yaw) * y + Math.sin(yaw) * x, turn);
	}

	/*
	 * Compares the absolute encoder to the motor encoder to find each motor's offset
	 * This helps each wheel to always be aligned
	 */
	public void setEncoderOffset() {
		offsetFR = (m_SteerFR.getSensorPosition() / encoderConversionFactor) % 360 - e_FrontRight.getAbsolutePosition();
		offsetFL = (m_SteerFL.getSensorPosition() / encoderConversionFactor) % 360 - e_FrontLeft.getAbsolutePosition();
		offsetBR = (m_SteerBR.getSensorPosition() / encoderConversionFactor) % 360 - e_BackRight.getAbsolutePosition();
		offsetBL = (m_SteerBL.getSensorPosition() / encoderConversionFactor) % 360 - e_BackLeft.getAbsolutePosition();
	}

	/*
	 * Stops each drive motor
	 */
	public void stopDrive() {
		checkContextOwnership();
		m_DriveFR.stopMotor();
		m_DriveFL.stopMotor();
		m_DriveBR.stopMotor();
		m_DriveBL.stopMotor();
	}
}