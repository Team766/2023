package com.team766.robot.mechanisms;

import com.ctre.phoenix.sensors.CANCoder;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController.ControlMode;
import com.team766.logging.Category;
import com.team766.robot.constants.SwerveDriveConstants;
import com.team766.robot.constants.OdometryInputConstants;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import com.team766.odometry.Odometry;
import com.team766.odometry.Point;
import com.team766.odometry.PointDir;

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
	private final double ENCODER_CONVERSION_FACTOR = (150.0 / 7.0) /*steering gear ratio*/ * (2048.0 / 360.0) /*encoder units to degrees*/;

	// TODO: rework odometry so it doesn't have to go through drive

	// declaration of odometry object
	private Odometry swerveOdometry;
	// variable representing current position
	private static PointDir currentPosition;

	// other variables to set up odometry
	private MotorController[] motorList;
	private CANCoder[] CANCoderList;
	private Point[] wheelPositions;

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
		e_FrontRight = new CANCoder(22, SwerveDriveConstants.SWERVE_CANBUS);
		e_FrontLeft = new CANCoder(23, SwerveDriveConstants.SWERVE_CANBUS);
		e_BackRight = new CANCoder(21, SwerveDriveConstants.SWERVE_CANBUS);
		e_BackLeft = new CANCoder(24, SwerveDriveConstants.SWERVE_CANBUS);

		// Current limit for motors to avoid breaker problems 
		m_DriveFR.setCurrentLimit(SwerveDriveConstants.DRIVE_MOTOR_CURRENT_LIMIT);
		m_DriveFL.setCurrentLimit(SwerveDriveConstants.DRIVE_MOTOR_CURRENT_LIMIT);
		m_DriveBR.setCurrentLimit(SwerveDriveConstants.DRIVE_MOTOR_CURRENT_LIMIT);
		m_DriveBL.setCurrentLimit(SwerveDriveConstants.DRIVE_MOTOR_CURRENT_LIMIT);
		m_SteerFR.setCurrentLimit(SwerveDriveConstants.STEER_MOTOR_CURRENT_LIMIT);
		m_SteerFL.setCurrentLimit(SwerveDriveConstants.STEER_MOTOR_CURRENT_LIMIT);
		m_SteerBR.setCurrentLimit(SwerveDriveConstants.STEER_MOTOR_CURRENT_LIMIT);
		m_SteerBL.setCurrentLimit(SwerveDriveConstants.STEER_MOTOR_CURRENT_LIMIT);

		// Sets up odometry
		currentPosition = new PointDir(0, 0, 0);
		motorList = new MotorController[] {m_DriveFR, m_DriveFL, m_DriveBL,
				m_DriveBR};
		CANCoderList = new CANCoder[] {e_FrontRight, e_FrontLeft, e_BackLeft, e_BackRight};
		wheelPositions =
				new Point[] {new Point(OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2, OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2),
						new Point(OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2, -OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2),
						new Point(-OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2, -OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2),
						new Point(-OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2, OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2)};
		log("MotorList Length: " + motorList.length);
		log("CANCoderList Length: " + CANCoderList.length);
		swerveOdometry = new Odometry(motorList, CANCoderList, wheelPositions, OdometryInputConstants.WHEEL_CIRCUMFERENCE, OdometryInputConstants.GEAR_RATIO, OdometryInputConstants.ENCODER_TO_REVOLUTION_CONSTANT, OdometryInputConstants.RATE_LIMITER_TIME);

		// Sets the offset value for each steering motor so that each is aligned
		setEncoderOffset();
	}

	/**
	 * Sets just the steer for a specific module
	 * Can be used to turn the wheels without moving
	 * @param steerMotor the steer motor of this module
	 * @param vector the vector specifying the module's motion
	 * @param offset the offset for this module
	 */
	public void controlModuleSteer(MotorController steerMotor, Vector2D vector, double offset) {

		// Calculates the angle of the vector from -180° to 180°
		final double vectorTheta = Math.toDegrees(Math.atan2(vector.getY(), vector.getX()));

		// Add 360 * number of full rotations to vectorTheta, then add offset
		final double angleDegrees = vectorTheta + 360*(Math.round((steerMotor.getSensorPosition()/ENCODER_CONVERSION_FACTOR - offset - vectorTheta)/360)) + offset;

		// Sets the degree of the steer wheel
		// Needs to multiply by encoderconversionfactor to translate into a unit the motor understands
		steerMotor.set(ControlMode.Position, ENCODER_CONVERSION_FACTOR*angleDegrees);

		SmartDashboard.putNumber("Angle", angleDegrees);
	}

	/**
	 * Sets the motion for a specific module
	 * @param driveMotor the drive motor of this module
	 * @param steerMotor the steer motor of this module
	 * @param vector the vector specifying the module's motion
	 * @param offset the offset for this module
	 */
	public void controlModuleSteerAndPower(MotorController driveMotor, MotorController steerMotor, Vector2D vector, double offset) {
		checkContextOwnership();

		controlModuleSteer(steerMotor, vector, offset);

		// Sets the power to the magnitude of the vector
		driveMotor.set(vector.getNorm());

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
		controlModuleSteerAndPower(m_DriveFL, m_SteerFL, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.FL_X, SwerveDriveConstants.FL_Y).normalize()), offsetFL);
		controlModuleSteerAndPower(m_DriveFR, m_SteerFR, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.FR_X, SwerveDriveConstants.FR_Y).normalize()), offsetFR);
		controlModuleSteerAndPower(m_DriveBR, m_SteerBR, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.BR_X, SwerveDriveConstants.BR_Y).normalize()), offsetBR);
		controlModuleSteerAndPower(m_DriveBL, m_SteerBL, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.BL_X, SwerveDriveConstants.BL_Y).normalize()), offsetBL);
	}

	/**
	 * Uses controlRobotOriented() to control the robot relative to the field
	 * @param yawRad the robot gyro's current yaw value in radians
	 * @param x the x value for the position joystick
	 * @param y the y value for the position joystick
	 * @param turn the turn value from the rotation joystick
	 */
	public void controlFieldOriented(double yawRad, double x, double y, double turn) {
		// Applies a rotational translation to controlRobotOriented
		// Counteracts the forward direction changing when the robot turns
		controlRobotOriented(Math.cos(yawRad) * x - Math.sin(yawRad) * y, Math.cos(yawRad) * y + Math.sin(yawRad) * x, turn);
	}

	/*
	 * Compares the absolute encoder to the motor encoder to find each motor's offset
	 * This helps each wheel to always be aligned
	 */
	public void setEncoderOffset() {
		offsetFR = (m_SteerFR.getSensorPosition() / ENCODER_CONVERSION_FACTOR) % 360 - e_FrontRight.getAbsolutePosition();
		offsetFL = (m_SteerFL.getSensorPosition() / ENCODER_CONVERSION_FACTOR) % 360 - e_FrontLeft.getAbsolutePosition();
		offsetBR = (m_SteerBR.getSensorPosition() / ENCODER_CONVERSION_FACTOR) % 360 - e_BackRight.getAbsolutePosition();
		offsetBL = (m_SteerBL.getSensorPosition() / ENCODER_CONVERSION_FACTOR) % 360 - e_BackLeft.getAbsolutePosition();
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

	/*
	 * Turns wheels in a cross formation to prevent robot from moving
	 */
	public void setCross() {
		controlModuleSteer(m_SteerFL, new Vector2D(SwerveDriveConstants.FL_Y, -SwerveDriveConstants.FL_X), offsetFL);
		controlModuleSteer(m_SteerFR, new Vector2D(SwerveDriveConstants.FR_Y, -SwerveDriveConstants.FR_X), offsetFR);
		controlModuleSteer(m_SteerBL, new Vector2D(SwerveDriveConstants.BL_Y, -SwerveDriveConstants.BL_X), offsetBL);
		controlModuleSteer(m_SteerBR, new Vector2D(SwerveDriveConstants.BR_Y, -SwerveDriveConstants.BR_X), offsetBR);
	}

	// TODO: rework odometry so it doesn't have to go through drive
	// TODO: figure out why odometry x and y are swapped
	public PointDir getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(Point P) {
		swerveOdometry.setCurrentPosition(P);
	}

	public void resetCurrentPosition() {
		swerveOdometry.setCurrentPosition(new Point(0, 0));
	}

	// Odometry
	@Override
	public void run() {
		currentPosition = swerveOdometry.run();
		log(currentPosition.toString());
		SmartDashboard.putString("position", currentPosition.toString());
	}
}