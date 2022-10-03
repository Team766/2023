package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController;

public class ExampleMechanism extends Mechanism {
	private MotorController m_leftMotor;
	private MotorController m_rightMotor;

	public ExampleMechanism() {
		m_leftMotor = RobotProvider.instance.getMotor("exampleMechanism.leftMotor");
		m_rightMotor = RobotProvider.instance.getMotor("exampleMechanism.rightMotor");
	}

	public void setMotorPower(double leftPower, double rightPower){
		checkContextOwnership();

		m_leftMotor.set(leftPower);
		m_rightMotor.set(rightPower);
	}
}