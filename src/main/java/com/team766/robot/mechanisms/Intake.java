package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;

public class Intake extends Mechanism {
	private SolenoidController leftPiston;
	private SolenoidController rightPiston;
	private MotorController topBelt;
	private MotorController bottomWheels;
	
	public Intake() {
		topBelt = RobotProvider.instance.getMotor("Intake.topWheels");
		bottomWheels = RobotProvider.instance.getMotor("Intake.bottomWheels");

		leftPiston = RobotProvider.instance.getSolenoid("Intake.leftPiston");
		rightPiston = RobotProvider.instance.getSolenoid("Intake.rightPiston");

	}

	public void startIntake() {
		checkContextOwnership();
		
		startArms();
		topBelt.set(1.0);
		bottomWheels.set(1.0);

	}

	public void stopIntake() {
		checkContextOwnership();
		
		topBelt.set(0.0);
		bottomWheels.set(0.0);
		stopArms();
	}

	public void startArms(){
		checkContextOwnership();

		leftPiston.set(true);
		rightPiston.set(true);
	}

	public void stopArms(){
		checkContextOwnership();

		leftPiston.set(false);
		rightPiston.set(false);
	}

	public void reverseIntake(){
		checkContextOwnership();

		topBelt.set(-1.0);
		bottomWheels.set(-1.0);

		startArms();
	}
}