package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.EncoderReader;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.simulator.Encoder;
import com.team766.hal.MotorController;
import com.team766.library.RateLimiter;
import com.team766.library.ValueProvider;
import com.team766.logging.Category;
import com.team766.simulator.ProgramInterface.RobotMode;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.team766.odometry.Odometry;
import com.team766.odometry.Point;
import com.team766.odometry.PointDir;
import com.team766.hal.MotorControllerCommandFailedException;
import com.team766.robot.constants.*;

public class Drive extends Mechanism {

	private MotorController m_DriveFrontRight;
	private MotorController m_DriveFrontLeft;
	private MotorController m_DriveBackRight;
	private MotorController m_DriveBackLeft;

	private MotorController m_SteerFrontRight;
	private MotorController m_SteerFrontLeft;
	private MotorController m_SteerBackRight;
	private MotorController m_SteerBackLeft;

	private CANCoder e_FrontRight;
	private CANCoder e_FrontLeft;
	private CANCoder e_BackRight;
	private CANCoder e_BackLeft;

	private ValueProvider<Double> drivePower;

	private double gyroValue;

	private static PointDir currentPosition;

	private MotorController[] motorList;
	private CANCoder[] CANCoderList;
	private Point[] wheelPositions;
	private Odometry swerveOdometry;

	public Drive() {

		loggerCategory = Category.DRIVE;
		// Initializations of motors
		// Initialize the drive motors
		m_DriveFrontRight = RobotProvider.instance.getMotor("drive.DriveFrontRight");
		m_DriveFrontLeft = RobotProvider.instance.getMotor("drive.DriveFrontLeft");
		m_DriveBackRight = RobotProvider.instance.getMotor("drive.DriveBackRight");
		m_DriveBackLeft = RobotProvider.instance.getMotor("drive.DriveBackLeft");
		// Initialize the steering motors
		m_SteerFrontRight = RobotProvider.instance.getMotor("drive.SteerFrontRight");
		m_SteerFrontLeft = RobotProvider.instance.getMotor("drive.SteerFrontLeft");
		m_SteerBackRight = RobotProvider.instance.getMotor("drive.SteerBackRight");
		m_SteerBackLeft = RobotProvider.instance.getMotor("drive.SteerBackLeft");

		// Setting up the "config"
		CANCoderConfiguration config = new CANCoderConfiguration();
		config.absoluteSensorRange = AbsoluteSensorRange.Signed_PlusMinus180;
		// The encoders output "encoder" values, so we need to convert that to degrees (because that
		// is what the cool kids are using)
		config.sensorCoefficient = 360.0 / 4096.0;
		// The offset is going to be changed in ctre, but we can change it here too.
		// config.magnetOffsetDegrees = Math.toDegrees(configuration.getOffset());
		config.sensorDirection = true;

		// initialize the encoders
		e_FrontRight = new CANCoder(22, "Swervavore");
		// e_FrontRight.configAllSettings(config, 250);
		e_FrontLeft = new CANCoder(23, "Swervavore");
		// e_FrontLeft.configAllSettings(config, 250);
		e_BackRight = new CANCoder(21, "Swervavore");
		// e_BackRight.configAllSettings(config, 250);
		e_BackLeft = new CANCoder(24, "Swervavore");
		// e_BackLeft.configAllSettings(config, 250);


		// Current limit for motors to avoid breaker problems (mostly to avoid getting electrical
		// people to yell at us)
		m_DriveFrontRight.setCurrentLimit(35);
		m_DriveFrontLeft.setCurrentLimit(35);
		m_DriveBackRight.setCurrentLimit(35);
		m_DriveBackLeft.setCurrentLimit(35);
		m_DriveBackLeft.setInverted(true);
		m_DriveBackRight.setInverted(true);
		m_SteerFrontRight.setCurrentLimit(30);
		m_SteerFrontLeft.setCurrentLimit(30);
		m_SteerBackRight.setCurrentLimit(30);
		m_SteerBackLeft.setCurrentLimit(30);

		// Setting up the connection between steering motors and cancoders
		// m_SteerFrontRight.setRemoteFeedbackSensor(e_FrontRight, 0);
		// m_SteerFrontLeft.setRemoteFeedbackSensor(e_FrontLeft, 0);
		// m_SteerBackRight.setRemoteFeedbackSensor(e_BackRight, 0);
		// m_SteerBackLeft.setRemoteFeedbackSensor(e_BackLeft, 0);

		m_SteerFrontRight.setSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
		m_SteerFrontLeft.setSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
		m_SteerBackRight.setSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
		m_SteerBackLeft.setSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
		configPID();

		// Sets up odometry
		currentPosition = new PointDir(0, 0, 0);
		motorList = new MotorController[] {m_DriveFrontRight, m_DriveFrontLeft, m_DriveBackLeft,
				m_DriveBackRight};
		CANCoderList = new CANCoder[] {e_FrontRight, e_FrontLeft, e_BackLeft, e_BackRight};
		wheelPositions =
				new Point[] {new Point(OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2, OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2),
						new Point(OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2, -OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2),
						new Point(-OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2, -OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2),
						new Point(-OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2, OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2)};
		log("MotorList Length: " + motorList.length);
		log("CANCoderList Length: " + CANCoderList.length);
		swerveOdometry = new Odometry(motorList, CANCoderList, wheelPositions, OdometryInputConstants.WHEEL_CIRCUMFERENCE, OdometryInputConstants.GEAR_RATIO, OdometryInputConstants.ENCODER_TO_REVOLUTION_CONSTANT, OdometryInputConstants.RATE_LIMITER_TIME);
	}

	// A set of simple functions for the sake of adding vectors
	/**
	 * Returns the pythagorean theorem of two numbers
	 * 
	 * @param x First number
	 * @param y Second number
	 * @return
	 */
	public double pythagorean(double x, double y) {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	/**
	 * Returns the angle of a vector, used by joystick inputs
	 * 
	 * @param LR The x value of the vector
	 * @param FB The y value of the vector
	 * @return The angle of the vector
	 */
	public double getAngle(double LR, double FB) {
		return Math.toDegrees(Math.atan2(LR, -FB));
	}

	/**
	 * Returns whether two angles are within 90 degrees of each other, used to see if the wheels
	 * should move backwards or not
	 * 
	 * @param angle1 The first angle
	 * @param angle2 The second angle
	 * @return If they are within 90 degrees of each other
	 */
	public boolean withinHalfACircle(double angle1, double angle2) {
		angle1 = mod(angle1, 360);
		angle2 = mod(angle2, 360);
		if (Math.abs(angle2 - angle1) > Math.abs(angle2 + 360 - angle1)) {
			angle2 += 360;
		}
		if (Math.abs(angle2 - angle1) > Math.abs(angle2 - 360 - angle1)) {
			angle2 -= 360;
		}
		return Math.abs(angle2 - angle1) <= 90;
	}

	// Returns mod(d1, d2), to use to circumvent java's weird % function
	private static double mod(double d1, double d2) {
		return d1 % d2 + (d1 < 0 ? d2 : 0);
	}

	/**
	 * Rounds a number based on its value and places
	 * 
	 * @param value The number to be rounded
	 * @param places The number of places to round to
	 * @return The rounded number
	 */
	public double round(double value, int places) {
		double scale = Math.pow(10, places);
		return Math.round(value * scale) / scale;
	}

	/**
	 * Adds two vectors together
	 * 
	 * @param FirstMag The magnitude of the first vector
	 * @param FirstAng The angle of the first vector
	 * @param SecondMag The magnitude of the second vector
	 * @param SecondAng The angle of the second vector
	 * @return New angle of the vector
	 */
	public double NewAng(double FirstMag, double FirstAng, double SecondMag, double SecondAng) {
		double FinalX = FirstMag * Math.cos(Math.toRadians(FirstAng))
				+ SecondMag * Math.cos(Math.toRadians(SecondAng));
		double FinalY = FirstMag * Math.sin(Math.toRadians(FirstAng))
				+ SecondMag * Math.sin(Math.toRadians(SecondAng));
		return round(Math.toDegrees(Math.atan2(FinalY, FinalX)), 5);
	}

	/**
	 * Adds two vectors together
	 * 
	 * @param FirstMag The magnitude of the first vector
	 * @param FirstAng The angle of the first vector
	 * @param SecondMag The magnitude of the second vector
	 * @param SecondAng The angle of the second vector
	 * @return New magnitude of the vector
	 */
	public double NewMag(double FirstMag, double FirstAng, double SecondMag, double SecondAng) {
		double FinalX = FirstMag * Math.cos(Math.toRadians(FirstAng))
				+ SecondMag * Math.cos(Math.toRadians(SecondAng));
		double FinalY = FirstMag * Math.sin(Math.toRadians(FirstAng))
				+ SecondMag * Math.sin(Math.toRadians(SecondAng));
		return round(Math.sqrt(Math.pow(FinalX, 2) + Math.pow(FinalY, 2)), 5);
	}

	/**
	 * Corrects the joystick inputs to make them more accurate, currently unused
	 * 
	 * @param Joystick The joystick value to be corrected
	 * @returnThe corrected joystick value
	 */
	public static double correctedJoysticks(double Joystick) {
		if (Joystick >= 0)
			return (3.0 * Math.pow(Joystick, 2) - 2.0 * Math.pow(Joystick, 3));
		else
			return (-1 * 3.0 * Math.pow(-1 * Joystick, 2) + 2.0 * Math.pow(-1 * Joystick, 3));
	}

	/**
	 * Converts the angle of the joystick to the angle of the robot compared to the field by using
	 * the gyro
	 * 
	 * @param angle The angle of the joystick
	 * @param gyro The angle of the gyro
	 * @return The angle of the robot compared to the field
	 */
	public static double fieldAngle(double angle, double gyro) {
		double newAngle;
		newAngle = angle - gyro;
		if (newAngle < 0) {
			newAngle = newAngle + 360;
		}
		if (newAngle >= 180) {
			newAngle = newAngle - 360;
		}
		return newAngle;
	}

	/**
	 * Method to correct angles for the falcon encoders
	 * 
	 * @param newAngle The given angle value
	 * @param lastAngle The last angle value
	 * @return The corrected angle value
	 */
	public static double newAngle(double newAngle, double lastAngle) {
		while (newAngle < 0)
			newAngle += 360;
		while (newAngle < (lastAngle - 180))
			newAngle += 360;
		while (newAngle > (lastAngle + 180))
			newAngle -= 360;
		return newAngle;
	}

	/**
	 * Sets the gyro value, used to switch between field and robot orientation
	 * 
	 * @param value The value to set the gyro to
	 */
	public void setGyro(double value) {
		gyroValue = value;
	}

	/**
	 * This method is used to drive the robot in 2D without turning, using the joystick values.
	 * 
	 * @param JoystickX The x value of the joystick
	 * @param JoystickY The y value of the joystick
	 */
	public void drive2D(double JoystickX, double JoystickY) {
		checkContextOwnership();
		// double power = pythagorean((JoystickX), correctedJoysticks(JoystickY))/Math.sqrt(2);
		double power = Math.max(Math.abs(JoystickX), Math.abs(JoystickY));
		double angle = fieldAngle(getAngle(JoystickX, JoystickY), gyroValue);


		if (withinHalfACircle(angle, getCurrentAngle(m_SteerFrontRight))) {
			m_DriveFrontRight.set(power);
			setFrontRightAngle(newAngle(angle, getCurrentAngle(m_SteerFrontRight)));
		} else {
			m_DriveFrontRight.set(-power);
			setFrontRightAngle(newAngle(180 + angle, getCurrentAngle(m_SteerFrontRight)));
		}

		if (withinHalfACircle(angle, getCurrentAngle(m_SteerFrontLeft))) {
			m_DriveFrontLeft.set(power);
			setFrontLeftAngle(newAngle(angle, getCurrentAngle(m_SteerFrontLeft)));
		} else {
			m_DriveFrontLeft.set(-power);
			setFrontLeftAngle(newAngle(180 + angle, getCurrentAngle(m_SteerFrontLeft)));
		}

		if (withinHalfACircle(angle, getCurrentAngle(m_SteerBackRight))) {
			m_DriveBackRight.set(power);
			setBackRightAngle(newAngle(angle, getCurrentAngle(m_SteerBackRight)));
		} else {
			m_DriveBackRight.set(-power);
			setBackRightAngle(newAngle(180 + angle, getCurrentAngle(m_SteerBackRight)));
		}

		if (withinHalfACircle(angle, getCurrentAngle(m_SteerBackLeft))) {
			m_DriveBackLeft.set(power);
			setBackLeftAngle(newAngle(angle, getCurrentAngle(m_SteerBackLeft)));
		} else {
			m_DriveBackLeft.set(-power);
			setBackLeftAngle(newAngle(180 + angle, getCurrentAngle(m_SteerBackLeft)));
		}
	}

	/**
	 * This method is used to drive the robot in 2D without turning, using a point.
	 * 
	 * @param joystick The point to use for the joystick values
	 */
	public void drive2D(Point joystick) {
		drive2D(joystick.getX(), joystick.getY());
	}

	/**
	 * This method stops all of the drive motors
	 */
	public void stopDriveMotors() {
		checkContextOwnership();
		m_DriveFrontRight.stopMotor();
		m_DriveFrontLeft.stopMotor();
		m_DriveBackRight.stopMotor();
		m_DriveBackLeft.stopMotor();
	}

	/**
	 * This method stops all of the steer motors
	 */
	public void stopSteerMotors() {
		checkContextOwnership();
		m_SteerFrontRight.stopMotor();
		m_SteerFrontLeft.stopMotor();
		m_SteerBackRight.stopMotor();
		m_SteerBackLeft.stopMotor();
	}

	/**
	 * This method is the main method for driving the robot, using the joystick values.
	 * 
	 * @param JoystickX The x value of the joystick
	 * @param JoystickY The y value of the joystick
	 * @param JoystickTheta The theta value of the joystick (for turning)
	 */
	public void swerveDrive(double JoystickX, double JoystickY, double JoystickTheta) {
		checkContextOwnership();
		double power = Math.max(Math.abs(JoystickX), Math.abs(JoystickY));
		double angle = fieldAngle(getAngle(JoystickX, JoystickY), gyroValue);
		double frPower;
		double flPower;
		double brPower;
		double blPower;
		double frAngle;
		double flAngle;
		double brAngle;
		double blAngle;
		if (JoystickTheta >= 0) {
			frPower = NewMag(power, angle, JoystickTheta, 45);
			flPower = NewMag(power, angle, JoystickTheta, -45);
			brPower = NewMag(power, angle, JoystickTheta, 135);
			blPower = NewMag(power, angle, JoystickTheta, -135);
			frAngle = NewAng(power, angle, JoystickTheta, 45);
			flAngle = NewAng(power, angle, JoystickTheta, 135);
			brAngle = NewAng(power, angle, JoystickTheta, -45);
			blAngle = NewAng(power, angle, JoystickTheta, -135);
		} else {
			frPower = NewMag(power, angle, Math.abs(JoystickTheta), -135);
			flPower = NewMag(power, angle, Math.abs(JoystickTheta), 135);
			brPower = NewMag(power, angle, Math.abs(JoystickTheta), -45);
			blPower = NewMag(power, angle, Math.abs(JoystickTheta), 45);
			frAngle = NewAng(power, angle, Math.abs(JoystickTheta), -135);
			flAngle = NewAng(power, angle, Math.abs(JoystickTheta), -45);
			brAngle = NewAng(power, angle, Math.abs(JoystickTheta), 135);
			blAngle = NewAng(power, angle, Math.abs(JoystickTheta), 45);
		}
		if (Math.max(Math.max(frPower, flPower), Math.max(brPower, blPower)) > 1) {
			frPower /= Math.max(Math.max(frPower, flPower), Math.max(brPower, blPower));
			flPower /= Math.max(Math.max(frPower, flPower), Math.max(brPower, blPower));
			brPower /= Math.max(Math.max(frPower, flPower), Math.max(brPower, blPower));
			blPower /= Math.max(Math.max(frPower, flPower), Math.max(brPower, blPower));
		}

		if (withinHalfACircle(frAngle, getCurrentAngle(m_SteerFrontRight))) {
			m_DriveFrontRight.set(frPower);
			setFrontRightAngle(newAngle(frAngle, getCurrentAngle(m_SteerFrontRight)));
		} else {
			m_DriveFrontRight.set(-frPower);
			setFrontRightAngle(newAngle(180 + frAngle, getCurrentAngle(m_SteerFrontRight)));
		}

		if (withinHalfACircle(flAngle, getCurrentAngle(m_SteerFrontLeft))) {
			m_DriveFrontLeft.set(flPower);
			setFrontLeftAngle(newAngle(flAngle, getCurrentAngle(m_SteerFrontLeft)));
		} else {
			m_DriveFrontLeft.set(-flPower);
			setFrontLeftAngle(newAngle(180 + flAngle, getCurrentAngle(m_SteerFrontLeft)));
		}

		if (withinHalfACircle(brAngle, getCurrentAngle(m_SteerBackRight))) {
			m_DriveBackRight.set(brPower);
			setBackRightAngle(newAngle(brAngle, getCurrentAngle(m_SteerBackRight)));
		} else {
			m_DriveBackRight.set(-brPower);
			setBackRightAngle(newAngle(180 + brAngle, getCurrentAngle(m_SteerBackRight)));
		}

		if (withinHalfACircle(blAngle, getCurrentAngle(m_SteerBackLeft))) {
			m_DriveBackLeft.set(blPower);
			setBackLeftAngle(newAngle(blAngle, getCurrentAngle(m_SteerBackLeft)));
		} else {
			m_DriveBackLeft.set(-blPower);
			setBackLeftAngle(newAngle(180 + blAngle, getCurrentAngle(m_SteerBackLeft)));
		}
	}

	/**
	 * This method is used to drive the robot with swerve using a PointDir
	 * 
	 * @param joystick The PointDir to use for the joystick values
	 */
	public void swerveDrive(PointDir joystick) {
		swerveDrive(-1 * joystick.getY(), -1 * joystick.getX(), joystick.getHeading());
	}

	/**
	 * This method is used to simply turn the robot without moving it
	 * 
	 * @param Joystick The joystick value to use for turning
	 */
	public void turning(double Joystick) {
		checkContextOwnership();
		if (Joystick > 0) {
			setFrontRightAngle(newAngle(135, getCurrentAngle(m_SteerFrontRight)));
			setFrontLeftAngle(newAngle(45, getCurrentAngle(m_SteerFrontLeft)));
			setBackRightAngle(newAngle(-135, getCurrentAngle(m_SteerBackRight)));
			setBackLeftAngle(newAngle(-45, getCurrentAngle(m_SteerBackLeft)));
			m_DriveFrontRight.set(Math.abs(Joystick));
			m_DriveFrontLeft.set(Math.abs(Joystick));
			m_DriveBackRight.set(Math.abs(Joystick));
			m_DriveBackLeft.set(Math.abs(Joystick));
		}
		if (Joystick < 0) {
			setFrontRightAngle(newAngle(-45, getCurrentAngle(m_SteerFrontRight)));
			setFrontLeftAngle(newAngle(-135, getCurrentAngle(m_SteerFrontLeft)));
			setBackRightAngle(newAngle(45, getCurrentAngle(m_SteerBackRight)));
			setBackLeftAngle(newAngle(135, getCurrentAngle(m_SteerBackLeft)));
			m_DriveFrontRight.set(Math.abs(Joystick));
			m_DriveFrontLeft.set(Math.abs(Joystick));
			m_DriveBackRight.set(Math.abs(Joystick));
			m_DriveBackLeft.set(Math.abs(Joystick));
		}
	}

	private double getCurrentAngle(MotorController motor) {
		return Math.pow((2048.0 / 360.0 * (150.0 / 7.0)), -1) * motor.getSensorPosition();
	}

	/**
	 * Simple encoder logging method
	 */
	public void logs() {
		log("Front Right Encoder: " + getFrontRight() + " Front Left Encoder: " + getFrontLeft()
				+ " Back Right Encoder: " + getBackRight() + " Back Left Encoder: "
				+ getBackLeft());
	}

	/**
	 * This method is used to set the front right encoder to the true position
	 */
	public void setFrontRightEncoders() {
		m_SteerFrontRight.setSensorPosition((int) Math
				.round(2048.0 / 360.0 * (150.0 / 7.0) * e_FrontRight.getAbsolutePosition()));
	}

	/**
	 * This method is used to set the front left encoder to the true position
	 */
	public void setFrontLeftEncoders() {
		m_SteerFrontLeft.setSensorPosition((int) Math
				.round(2048.0 / 360.0 * (150.0 / 7.0) * e_FrontLeft.getAbsolutePosition()));

	}

	/**
	 * This method is used to set the back right encoder to the true position
	 */
	public void setBackRightEncoders() {
		m_SteerBackRight.setSensorPosition((int) Math
				.round(2048.0 / 360.0 * (150.0 / 7.0) * e_BackRight.getAbsolutePosition()));
	}

	/**
	 * This method is used to set the back left encoder to the true position
	 */
	public void setBackLeftEncoders() {
		m_SteerBackLeft.setSensorPosition((int) Math
				.round(2048.0 / 360.0 * (150.0 / 7.0) * e_BackLeft.getAbsolutePosition()));
	}

	// To control each steering individually with a PID

	/**
	 * This method is used to set the front right steering motor to a certain angle. This uses a PID
	 * controller.
	 * 
	 * @param angle The angle to set the front right wheel to
	 */
	public void setFrontRightAngle(double angle) {
		// log("Angle: " + getFrontRight() + " || Motor angle: " + 360.0/ 2048.0 *
		// m_SteerFrontRight.getSensorPosition());
		m_SteerFrontRight.set(ControlMode.Position, 2048.0 / 360.0 * (150.0 / 7.0) * angle);
	}

	/**
	 * This method is used to set the front left steering motor to a certain angle. This uses a PID
	 * controller.
	 * 
	 * @param angle The angle to set the front left wheel to
	 */
	public void setFrontLeftAngle(double angle) {
		// log("Angle: " + getFrontLeft() + " || Motor angle: " + Math.pow((2048.0/360.0 *
		// (150.0/7.0)),-1) * m_SteerFrontLeft.getSensorPosition());
		m_SteerFrontLeft.set(ControlMode.Position, 2048.0 / 360.0 * (150.0 / 7.0) * angle);
	}

	/**
	 * This method is used to set the back right steering motor to a certain angle. This uses a PID
	 * controller.
	 * 
	 * @param angle The angle to set the back right wheel to
	 */
	public void setBackRightAngle(double angle) {
		// log("Angle: " + getBackRight() + " || Motor angle: " +
		// m_SteerBackRight.getSensorPosition());
		m_SteerBackRight.set(ControlMode.Position, 2048.0 / 360.0 * (150.0 / 7.0) * angle);
	}

	/**
	 * This method is used to set the back left steering motor to a certain angle. This uses a PID
	 * controller.
	 * 
	 * @param angle The angle to set the back left wheel to
	 */
	public void setBackLeftAngle(double angle) {
		// log("Angle: " + getBackLeft() + " || Motor angle: " +
		// m_SteerBackLeft.getSensorPosition());
		m_SteerBackLeft.set(ControlMode.Position, 2048.0 / 360.0 * (150.0 / 7.0) * angle);
	}

	/**
	 * Method to configure PID values. The values were pre-tuned and are not expected to change.
	 */
	public void configPID() {
		// PID for turning the various steering motors. Here is a good link to a tuning website:
		// https://www.robotsforroboticists.com/pid-control/
		m_SteerFrontRight.setP(0.2);
		m_SteerFrontRight.setI(0);
		m_SteerFrontRight.setD(0.1);
		m_SteerFrontRight.setFF(0);

		m_SteerFrontLeft.setP(0.2);
		m_SteerFrontLeft.setI(0);
		m_SteerFrontLeft.setD(0.1);
		m_SteerFrontLeft.setFF(0);

		m_SteerBackRight.setP(0.2);
		m_SteerBackRight.setI(0);
		m_SteerBackRight.setD(0.1);
		m_SteerBackRight.setFF(0);

		m_SteerBackLeft.setP(0.2);
		m_SteerBackLeft.setI(0);
		m_SteerBackLeft.setD(0.1);
		// m_SteerBackLeft.setFF(0);

		// pid values from sds for Flacons 500: P = 0.2 I = 0.0 D = 0.1 FF = 0.0

		// Code to invert sensors if needed. Recommended to do this in phoenix tuner.
		// m_SteerFrontRight.setSensorInverted(false);
		// m_SteerFrontLeft.setSensorInverted(false);
		// m_SteerBackRight.setSensorInverted(false);
		// m_SteerBackLeft.setSensorInverted(false);
	}

	// Methods to get the encoder values, the encoders are in degrees from -180 to 180. To change
	// that, we need to change the syntax and use getPosition()
	public double getFrontRight() {
		return e_FrontRight.getAbsolutePosition();
	}

	public double getFrontLeft() {
		return e_FrontLeft.getAbsolutePosition();
	}

	public double getBackRight() {
		return e_BackRight.getAbsolutePosition();
	}

	public double getBackLeft() {
		return e_BackLeft.getAbsolutePosition();
	}

	public PointDir getCurrentPosition() {
		return currentPosition;
	}

	public void setCross() {
		checkContextOwnership();
		setBackLeftAngle(newAngle(-45, getCurrentAngle(m_SteerBackLeft)));
		setFrontLeftAngle(newAngle(45, getCurrentAngle(m_SteerFrontLeft)));
		setFrontRightAngle(newAngle(135, getCurrentAngle(m_SteerFrontRight)));
		setBackRightAngle(newAngle(-135, getCurrentAngle(m_SteerBackRight)));
	}

	public void setCurrentPosition(Point P) {
		swerveOdometry.setCurrentPosition(P);
	}

	public void resetCurrentPosition() {
		swerveOdometry.setCurrentPosition(new Point(0, 0));
	}

	/**
	 * This method is used to reset the drive encoders.
	 */
	public void resetDriveEncoders() {
		m_DriveBackLeft.setSensorPosition(0);
		m_DriveBackRight.setSensorPosition(0);
		m_DriveFrontLeft.setSensorPosition(0);
		m_DriveFrontRight.setSensorPosition(0);
	}

	// Odometry
	@Override
	public void run() {
		currentPosition = swerveOdometry.run();
		log(currentPosition.toString());
	}
}

// AS
