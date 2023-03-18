package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.hal.wpilib.Solenoid;

public class Intake extends Mechanism {
	
	private MotorController bottomWheels;
	private MotorController topBelt;
	private SolenoidController leftPiston;
	private SolenoidController rightPiston;

	public Intake(){
		bottomWheels = RobotProvider.instance.getMotor("intake.bottomWheels");
		topBelt = RobotProvider.instance.getMotor("intake.topWheels");
		leftPiston = RobotProvider.instance.getSolenoid("intake.leftPiston");
		rightPiston = RobotProvider.instance.getSolenoid("intake.rightPiston");
	}

	public void intakeIn(){
		bottomWheels.set(1);
		topBelt.set(0.5);
		leftPiston.set(true);
		rightPiston.set(true);
	}

	public void intakeOut(){
		bottomWheels.set(-1);
		topBelt.set(-1);
		// leftPiston.set(true);
		// rightPiston.set(true);
	}

	public void intakeIdle(){
		bottomWheels.set(0);
		topBelt.set(0);
		leftPiston.set(false);
		rightPiston.set(false);
	}
}
