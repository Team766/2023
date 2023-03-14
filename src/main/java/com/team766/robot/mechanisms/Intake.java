package com.team766.frc2022.mechanisms;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.SolenoidController;
import com.team766.hal.CANSpeedController;
import com.team766.hal.RobotProvider;
import com.team766.hal.wpilib.DigitalInput;

public class Intake extends Mechanism {
	private SolenoidController intakeArm1;
	private SolenoidController intakeArm2;
	private MotorController topIntakeWheels;
	private MotorController bottomIntakeWheels;
	private MotorController conveyorBelt;
	private boolean beltGoing = false;
	
	public Intake() {
		topIntakeWheels = RobotProvider.instance.getMotor("Intake.topWheels");
		bottomIntakeWheels = RobotProvider.instance.getMotor("Intake.bottomWheels");
		conveyorBelt = RobotProvider.instance.getMotor("Intake.conveyorBelt");

		intakeArm1 = RobotProvider.instance.getSolenoid("Intake.leftPiston");
		intakeArm2 = RobotProvider.instance.getSolenoid("Intake.rightPiston");

	}

	public void startIntake() {
		checkContextOwnership();
		double power = ConfigFileReader.getInstance().getDouble("Intake.intakePower").get();
		
		startArms();
		topIntakeWheels.set(power);
		bottomIntakeWheels.set(power);
		conveyorBelt.set(power*0.5);

	}

	public void stopIntake() {
		checkContextOwnership();
		
		topIntakeWheels.set(0.0);
		bottomIntakeWheels.set(0.0);
		conveyorBelt.set(0.0);
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
	*	topIntakeWheels.set(0.0);
	*   bottomIntakeWheels.set(0.0);
	*	conveyorBelt.set(0.0);
	*	stopArms();
	* } else {
	*	startArms();
	*	topIntakeWheels.set(power);
	*	bottomIntakeWheels.set(power);
	*	conveyorBelt.set(power);
	* }
	* a different method will check if the sensor sees stuff
	* if(sensor1.getStorage || sensor2.getStrorage) stop the motors and arms
	*/

	public void startArms(){
		checkContextOwnership();

		intakeArm1.set(true);
		intakeArm2.set(true);
	}

	public void stopArms(){
		checkContextOwnership();

		intakeArm1.set(false);
		intakeArm2.set(false);
	}

	public void reverseIntake(){
		checkContextOwnership();
		double power = ConfigFileReader.getInstance().getDouble("Intake.intakePower").get();

		topIntakeWheels.set(-power);
		bottomIntakeWheels.set(-power);
		conveyorBelt.set(-power*0.5);

		startArms();
	}
}