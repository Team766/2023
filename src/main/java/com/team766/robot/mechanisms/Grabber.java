package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;

public class Grabber extends Mechanism {
	private SolenoidController grabby;

	public Grabber(){
		grabby = RobotProvider.instance.getSolenoid("arms.grabber");
	}

	public void grabberPickUp(){
		grabby.set(true);
	}

	public void grabberLetGo(){
		grabby.set(false);
	}
}
