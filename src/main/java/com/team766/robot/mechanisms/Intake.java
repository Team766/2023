package com.team766.frc2022.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.hal.wpilib.Solenoid;

public class Intake extends Mechanism {
	private SolenoidController leftPiston;
	private SolenoidController rightPiston;
	private MotorController topBelt;
	private MotorController bottomWheels;
	private boolean beltGoing = false;
	
	public Intake() {
		topBelt = RobotProvider.instance.getMotor("Intake.topWheels");
		bottomWheels = RobotProvider.instance.getMotor("Intake.bottomWheels");

		leftPiston = RobotProvider.instance.getSolenoid("Intake.leftPiston");
		rightPiston = RobotProvider.instance.getSolenoid("Intake.rightPiston");

	}

	public void startIntake() {
		checkContextOwnership();
		//double power = ConfigFileReader.getInstance().getDouble("Intake.intakePower").get();
		
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
	/*
	* Intake code that would use color sensor 
	* to automatically stop intake when piece reaches end of belt.
	* Can  be manually stopped by pressing button again.
	*
	* checkContextOwnership();
	* double power = ConfigFileReader.getInstance().getDouble("Intake.intakePower").get();
    *
	* if(beltGoing){
	*	topBelt.set(0.0);
	*   bottomWheels.set(0.0);
	*	conveyorBelt.set(0.0);
	*	stopArms();
	* } else {
	*	startArms();
	*	topBelt.set(1.0);
	*	bottomWheels.set(1.0);
	*	conveyorBelt.set(1.0);
	* }
	* a different method will check if the sensor sees stuff
	* if(sensor1.getStorage || sensor2.getStrorage) stop the motors and arms
	*/

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
		//double power = ConfigFileReader.getInstance().getDouble("Intake.intakePower").get();

		topBelt.set(-1.0);
		bottomWheels.set(-1.0);

		startArms();
	}
}