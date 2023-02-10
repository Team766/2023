package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController;

public class Arm extends Mechanism {
	private MotorController j1Motor;
	private MotorController j2Motor;

	public Arm() {
		j1Motor = RobotProvider.instance.getMotor("arm.j1Motor");
		j2Motor = RobotProvider.instance.getMotor("arm.j2Motor");
	}

	public void setMotorPower(double j1Power, double j2Power){
		checkContextOwnership();

		j1Motor.set(j1Power);
		j2Motor.set(j2Power);
	}
}