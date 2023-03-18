package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Arms extends Mechanism {

	private MotorController firstJoint;
	private MotorController secondJoint;
	private MotorController wrist;

	public Arms(){
		firstJoint = RobotProvider.instance.getMotor("arms.firstJoint");
		secondJoint = RobotProvider.instance.getMotor("arms.secondJoint");
		wrist = RobotProvider.instance.getMotor("arms.wrist");
	}
	
}
