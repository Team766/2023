package com.team766.robot.mechanisms;

import com.ctre.phoenix.sensors.CANCoder;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController.ControlMode;
import com.team766.logging.Category;
import com.team766.robot.constants.SwerveDriveConstants;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Drive extends Mechanism {

	private MotorController m_DriveFR;
	private MotorController m_DriveFL;
	private MotorController m_DriveBR;
	private MotorController m_DriveBL;

	private MotorController m_SteerFR;
	private MotorController m_SteerFL;
	private MotorController m_SteerBR;
	private MotorController m_SteerBL;

	private CANCoder e_FrontRight;
	private CANCoder e_FrontLeft;
	private CANCoder e_BackRight;
	private CANCoder e_BackLeft;

	private double offsetFR;
	private double offsetFL;
	private double offsetBR;
	private double offsetBL;

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

		// conversion from cancoder units to degrees: 360.0 / 4096.0;

		// initialize the encoders
		e_FrontRight = new CANCoder(22, "Swervavore");
		e_FrontLeft = new CANCoder(23, "Swervavore");
		e_BackRight = new CANCoder(21, "Swervavore");
		e_BackLeft = new CANCoder(24, "Swervavore");

		setEncoderOffset();
	}

	public void setModule(MotorController drive, MotorController steer, Vector2D vector, double offset) {
		steer.set(ControlMode.Position, Math.atan(vector.getX()/vector.getY()) + offset);
		drive.set(vector.getNorm());
	}

	public void controlRobotOriented(double x, double y, double turn) {
		setModule(m_DriveFL, m_SteerFL, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.fl_y, -SwerveDriveConstants.fl_x).normalize()), offsetFL);
		setModule(m_DriveFR, m_SteerFR, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.fr_y, -SwerveDriveConstants.fr_x).normalize()), offsetFR);
		setModule(m_DriveBR, m_SteerBR, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.br_y, -SwerveDriveConstants.br_x).normalize()), offsetBR);
		setModule(m_DriveBL, m_SteerBL, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.bl_y, -SwerveDriveConstants.bl_x).normalize()), offsetBL);
	}

	public void controlFieldOriented(double yaw, double x, double y, double turn) {
		controlRobotOriented(Math.cos(yaw) * x + Math.sin(yaw) * y, Math.cos(yaw) * y - Math.sin(yaw) * x, turn);
	}

	public void setEncoderOffset() {
		offsetFR = m_SteerFR.getSensorPosition() - e_FrontRight.getAbsolutePosition();
		offsetFL = m_SteerFL.getSensorPosition() - e_FrontLeft.getAbsolutePosition();
		offsetBR = m_SteerBR.getSensorPosition() - e_BackRight.getAbsolutePosition();
		offsetBL = m_SteerBL.getSensorPosition() - e_BackLeft.getAbsolutePosition();
	}
}