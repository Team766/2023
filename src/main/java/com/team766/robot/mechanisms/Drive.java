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

		

		// initialize the encoders
		e_FrontRight = new CANCoder(22, "Swervavore");
		e_FrontLeft = new CANCoder(23, "Swervavore");
		e_BackRight = new CANCoder(21, "Swervavore");
		e_BackLeft = new CANCoder(24, "Swervavore");

		setEncoderOffset();
		//configPID();
	}

	public void setModule(MotorController drive, MotorController steer, Vector2D vector, double offset) {
		//find degree bounded by -180º and 180º and add 360 * number of full rotations, then add offset
		double angleDegrees = Math.toDegrees(Math.atan2(vector.getY(), vector.getX())) + 360*(Math.round((steer.getSensorPosition()/encoderConversionFactor - offset - Math.toDegrees(Math.atan2(vector.getY(), vector.getX())))/360)) + offset;

		// needs to multiply by encoderconversionfactor to translate into a language the motor understands
		steer.set(ControlMode.Position, encoderConversionFactor*angleDegrees);
		drive.set(vector.getNorm());
		SmartDashboard.putNumber("Offset FR", offsetFR);
		SmartDashboard.putNumber("Offset FL", offsetFL);
		SmartDashboard.putNumber("Offset BR", offsetBR);
		SmartDashboard.putNumber("Offset BL", offsetBL);

		SmartDashboard.putNumber("Angle", angleDegrees);
	}

	public void controlRobotOriented(double x, double y, double turn) {
		//temporary testing fix (getting rid of negative on x)
		setModule(m_DriveFL, m_SteerFL, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.fl_x, SwerveDriveConstants.fl_y).normalize()), offsetFL);
		setModule(m_DriveFR, m_SteerFR, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.fr_x, SwerveDriveConstants.fr_y).normalize()), offsetFR);
		setModule(m_DriveBR, m_SteerBR, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.br_x, SwerveDriveConstants.br_y).normalize()), offsetBR);
		setModule(m_DriveBL, m_SteerBL, new Vector2D(x, y).add(turn, new Vector2D(SwerveDriveConstants.bl_x, SwerveDriveConstants.bl_y).normalize()), offsetBL);
	}

	public void controlFieldOriented(double yaw, double x, double y, double turn) {
		controlRobotOriented(Math.cos(yaw) * x - Math.sin(yaw) * y, Math.cos(yaw) * y + Math.sin(yaw) * x, turn);
	}

	public void setEncoderOffset() {
		offsetFR = (m_SteerFR.getSensorPosition()/encoderConversionFactor) % 360 - e_FrontRight.getAbsolutePosition();
		offsetFL = (m_SteerFL.getSensorPosition()/encoderConversionFactor) % 360 - e_FrontLeft.getAbsolutePosition();
		offsetBR = (m_SteerBR.getSensorPosition()/encoderConversionFactor) % 360 - e_BackRight.getAbsolutePosition();
		offsetBL = (m_SteerBL.getSensorPosition()/encoderConversionFactor) % 360 - e_BackLeft.getAbsolutePosition();
		SmartDashboard.putNumber("motor sensor FR", m_SteerFR.getSensorPosition());
		SmartDashboard.putNumber("motor encoder FR", e_FrontRight.getAbsolutePosition());
		SmartDashboard.putNumber("OffsetFR", offsetFR);

		SmartDashboard.putNumber("motor sensor BR", m_SteerBR.getSensorPosition());
		SmartDashboard.putNumber("motor encoder BR", e_BackRight.getAbsolutePosition());
		SmartDashboard.putNumber("OffsetBR", offsetBR);
	}

	public void stopDrive() {
		checkContextOwnership();
		m_DriveFR.stopMotor();
		m_DriveFL.stopMotor();
		m_DriveBR.stopMotor();
		m_DriveBL.stopMotor();
	}
}