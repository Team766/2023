/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team766.frc2018.mechanisms;

import com.team766.config.ConfigFileReader;
import com.team766.controllers.PIDController;
import com.team766.controllers.RangeOfMotionBound;
import com.team766.framework.Mechanism;
import com.team766.hal.EncoderReader;
import com.team766.hal.RobotProvider;
import com.team766.hal.SpeedController;

/**
 * An example subsystem.  You can replace me with your own Subsystem.
 */
public class ExampleMechanism extends Mechanism {
	
	public static final double FORWARD_POSITION = ConfigFileReader.getInstance().getDouble("exampleMechanism.forwardPosition");
	public static final double BACKWARD_POSITION = ConfigFileReader.getInstance().getDouble("exampleMechanism.backwardPosition");

	private PIDController m_controller;
	private RangeOfMotionBound m_bound;
	private EncoderReader m_encoder;
	private SpeedController m_motor;
	
	public ExampleMechanism() {
		m_controller = PIDController.loadFromConfig("exampleMechanism");
		m_bound = RangeOfMotionBound.loadFromConfig("exampleMechanism");
		m_encoder = RobotProvider.instance.getEncoder("exampleMechanism.encoder");
		m_motor = RobotProvider.instance.getMotor("exampleMechanism.motor");
	}
	
	public void run() {
		m_controller.calculate(m_encoder.getDistance(), true);
		double command = m_controller.getOutput();
		command = m_bound.filter(command, m_encoder.getDistance());
		m_motor.set(command);
	}
	
	public void setTargetPosition(double position) {
		m_controller.setSetpoint(position);
	}
	
	public void setTargetForward() {
		setTargetPosition(FORWARD_POSITION);
	}
	
	public void setTargetBackward() {
		setTargetPosition(BACKWARD_POSITION);
	}
	
	public boolean atTarget() {
		return m_controller.isDone();
	}
	
}
