package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;

public class Grabber extends Mechanism{
	
	private MotorController wrist;
	private SolenoidController grabber;

	public Grabber(){
		wrist = RobotProvider.instance.getMotor("wrist");
		grabber = RobotProvider.instance.getSolenoid("grabber");
	}

	public void grabIn(){
		grabber.set(true);
	}

	public void grabOut(){
		grabber.set(false);
	}
}
