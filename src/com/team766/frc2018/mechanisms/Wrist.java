package com.team766.frc2018.mechanisms;

import com.team766.config.ConfigFileReader;
import com.team766.controllers.MotionLockout;
import com.team766.controllers.PIDController;
import com.team766.framework.Mechanism;
import com.team766.frc2018.Robot;
import com.team766.hal.EncoderReader;
import com.team766.hal.RobotProvider;
import com.team766.hal.SpeedController;
import com.team766.library.ValueProvider;

public class Wrist extends Mechanism {
	
	public static final ValueProvider<Double> STOWED_POSITION =
			ConfigFileReader.getInstance().getDouble("wrist.stowedPosition");
	public static final ValueProvider<Double> VERTICAL_POSITION =
			ConfigFileReader.getInstance().getDouble("wrist.verticalPosition");
	public static final ValueProvider<Double> DOWN_POSITION =
			ConfigFileReader.getInstance().getDouble("wrist.downPosition");

	private Robot m_robot;
	private PIDController m_controller;
	private EncoderReader m_encoder;
	private MotionLockout m_armLockout;
	private SpeedController m_motor;
	
	private double m_commandedPosition;
	
	public Wrist(Robot robot) {
		m_robot = robot;
		m_controller = PIDController.loadFromConfig("wrist");
		m_encoder = RobotProvider.instance.getEncoder("wrist.encoder");
		m_armLockout = MotionLockout.loadFromConfig("wrist.armLockout");
		m_motor = RobotProvider.instance.getMotor("wrist.motor");
	}
	
	public void run() {
		double targetPosition = m_armLockout.filter(m_commandedPosition, m_robot.arm.getPosition());
		m_controller.setSetpoint(targetPosition);
		m_controller.calculate(getPosition(), true);
		double command = m_controller.getOutput();
		m_motor.set(command);
	}
	
	public double getPosition() {
		return m_encoder.getDistance();
	}
	
	public void setTargetPosition(double position) {
		m_commandedPosition = position;
	}
	
	public void setTargetStowed() {
		setTargetPosition(STOWED_POSITION.get());
	}
	
	public void setTargetVertical() {
		setTargetPosition(VERTICAL_POSITION.get());
	}
	
	public void setTargetDown() {
		setTargetPosition(DOWN_POSITION.get());
	}
	
	public boolean atTarget() {
		return m_controller.isDone();
	}
	
}
