package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;
import com.team766.hal.wpilib.CANSparkMaxMotorController;

public class Storage extends Mechanism {
	
	private MotorController belt;

	public Storage(){
		belt = RobotProvider.instance.getMotor("belt");
	}

	public void beltIn(){
		belt.set(1);
	}

	public void beltOut(){
		belt.set(-1);
	}

	public void beltIdle(){
		belt.set(0);
	}
}
