package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Grabber extends Mechanism {
	private MotorController grabby;

	public Grabber(){
		grabby = RobotProvider.instance.getMotor("arms.grabber");
	}

	public void grabberPickUp(){
		checkContextOwnership();
		grabby.set(1.0);

	}

	public void grabberLetGo(){
		checkContextOwnership();
		grabby.set(-1.0);
	}

	public void grabberStop(){
		checkContextOwnership();
		grabby.set(0.0);
	}
}
