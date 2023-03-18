package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Storage extends Mechanism {
	
	private MotorController belt;

	public Storage(){
		belt = RobotProvider.instance.getMotor("belt");
	}

	public void beltIn(){
		belt.set(0.5);
	}

	public void beltOut(){
		belt.set(-1.0);
	}

	public void beltIdle(){
		belt.set(0.0);
	}
}
