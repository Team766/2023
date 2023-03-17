package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Arms extends Mechanism {

	private MotorController firstJoint;
	private MotorController secondJoint;

	public Arms(){
		firstJoint = RobotProvider.instance.getMotor("arms.firstJoint");
		secondJoint = RobotProvider.instance.getMotor("arms.secondJoint");
	}
}
