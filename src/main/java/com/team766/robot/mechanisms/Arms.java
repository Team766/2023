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

	public void firstJointForward(double power){
		firstJoint.set(power);
	}

	public void firstJointBackward(double power){
		firstJoint.set(-1*power);
	}

	public void secondJointForward(double power){
		secondJoint.set(power);
	}

	public void secondJointBackward(double power){
		secondJoint.set(-1*power);
	}

	public void wristForward(double power){
		wrist.set(power);
	}

	public void wristBackward(double power){
		wrist.set(-1*power);
	}
}
