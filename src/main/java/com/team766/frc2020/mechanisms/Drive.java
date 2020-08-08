package com.team766.frc2020.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.EncoderReader;
import com.team766.hal.GyroReader;
import com.team766.hal.RobotProvider;
import com.team766.hal.SpeedController;

import com.team766.config.ConfigFileReader;

public class Drive extends Mechanism {
	
	private SpeedController m_leftMotor;
	private SpeedController m_rightMotor;
	private EncoderReader m_leftEncoder;
	private EncoderReader m_rightEncoder;
	private GyroReader m_gyro;
	
	// variables for calculating position using odometry
	private double m_x;
	private double m_y;
	private double m_previousLeft;
	private double m_previousRight;
	private double m_previousAngle;
	private double m_velocity;
	private double m_previousTime = RobotProvider.getTimeProvider().get();

	public Drive() {
		m_leftMotor = RobotProvider.instance.getMotor("drive.leftMotor");
		m_rightMotor = RobotProvider.instance.getMotor("drive.rightMotor");
		m_leftEncoder = RobotProvider.instance.getEncoder("drive.leftEncoder");
		m_rightEncoder = RobotProvider.instance.getEncoder("drive.rightEncoder");
		m_gyro = RobotProvider.instance.getGyro("drive.gyro");
	}
	
	@Override
	public void run() {
		double leftDistance = m_leftEncoder.getDistance();
		double rightDistance = m_rightEncoder.getDistance();
		double distance = ((leftDistance - m_previousLeft) + (rightDistance - m_previousRight)) / 2.0;
		
		// Use trapezoidal integration to get better position accuracy
		m_x += Math.cos(m_previousAngle) * distance / 2;
		m_y += Math.sin(m_previousAngle) * distance / 2;
		double angle = getHeadingAngle();
		m_x += Math.cos(angle) * distance / 2;
		m_y += Math.sin(angle) * distance / 2;
		
		m_previousLeft = leftDistance;
		m_previousRight = rightDistance;
		m_previousAngle = angle;

		// calculate velocity
		double currentTime = RobotProvider.getTimeProvider().get();
		m_velocity = distance / (currentTime - m_previousTime);
		m_previousTime = currentTime;
	}
	
	public double getHeadingAngle() {
		return m_gyro.getAngle() % 360;
	}
	
	public void resetGyro() {
		m_gyro.reset();
	}

	public double getXPosition() {
		return m_x;
	}
	
	public double getYPosition() {
		return m_y;
	}
	
	public void setDrivePower(double leftPower, double rightPower) {
		m_leftMotor.set(leftPower);
		m_rightMotor.set(rightPower);
	}

	public void setArcadeDrive(double fwdPower, double turnPower) {
		double maximum = Math.max(Math.abs(fwdPower), Math.abs(turnPower));
		double total = fwdPower + turnPower;
		double difference = fwdPower - turnPower;

		if (fwdPower >= 0) {
			if (turnPower >= 0) {
				setDrivePower(maximum, difference);
			} else {
				setDrivePower(total, maximum);
			}
		} else {
			if (turnPower >= 0) {
				setDrivePower(total, -maximum);
			} else {
				setDrivePower(-maximum, difference);
			}
		}
	}
}
