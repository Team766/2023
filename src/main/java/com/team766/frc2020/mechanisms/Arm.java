package com.team766.frc2018.mechanisms;

import com.team766.config.ConfigFileReader;
import com.team766.controllers.PIDController;
import com.team766.framework.Mechanism;
import com.team766.hal.EncoderReader;
import com.team766.hal.RobotProvider;
import com.team766.hal.SpeedController;
import com.team766.library.ValueProvider;

public class Arm extends Mechanism {
	
	public static final ValueProvider<Double> UP_POSITION =
			ConfigFileReader.getInstance().getDouble("arm.upPosition");
	public static final ValueProvider<Double> MID_POSITION =
			ConfigFileReader.getInstance().getDouble("arm.midPosition");
	public static final ValueProvider<Double> DOWN_POSITION =
			ConfigFileReader.getInstance().getDouble("arm.downPosition");

	private PIDController m_controller;
	private EncoderReader m_encoder;
	private SpeedController m_motor;
	
	private double m_commandedPosition;
	
	public Arm() {
		m_controller = PIDController.loadFromConfig("arm");
		m_encoder = RobotProvider.instance.getEncoder("arm.encoder");
		m_motor = RobotProvider.instance.getMotor("arm.motor");
	}
	
	public void run() {
		m_controller.setSetpoint(m_commandedPosition);
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
	
	public void setTargetUp() {
		setTargetPosition(UP_POSITION.get());
	}
	
	public void setTargetMid() {
		setTargetPosition(MID_POSITION.get());
	}
	
	public void setTargetDown() {
		setTargetPosition(DOWN_POSITION.get());
	}
	
	public boolean atTarget() {
		return m_controller.isDone();
	}
	
}
