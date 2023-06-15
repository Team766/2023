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

	}

	public void setModule(MotorController drive, MotorController steer, Vector2D vector) {
		steer.set(ControlMode.Position, Math.atan(vector.getX()/vector.getY()));
		drive.set(vector.getNorm());
	}

	public void control(double x, double y, double turn) {
		setModule(m_DriveFL, m_SteerFL, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.fl_y, -SwerveDriveConstants.fl_x).normalize()));
		setModule(m_DriveFR, m_SteerFR, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.fr_y, -SwerveDriveConstants.fr_x).normalize()));
		setModule(m_DriveBR, m_SteerBR, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.br_y, -SwerveDriveConstants.br_x).normalize()));
		setModule(m_DriveBL, m_SteerBL, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.bl_y, -SwerveDriveConstants.bl_x).normalize()));
	}
}