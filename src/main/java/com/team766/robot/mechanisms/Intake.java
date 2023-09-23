package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.hal.wpilib.Solenoid;
import com.team766.library.RateLimiter;

import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake extends Mechanism {
	
	private MotorController bottomWheels;
	private MotorController topBelt;
	private SolenoidController leftPiston;
	private SolenoidController rightPiston;
	private PneumaticHub ph;
	private RateLimiter runRateLimiter = new RateLimiter(0.5);
	
	public Intake() {
		topBelt = RobotProvider.instance.getMotor("intake.topWheels");
		bottomWheels = RobotProvider.instance.getMotor("intake.bottomWheels");

		leftPiston = RobotProvider.instance.getSolenoid("intake.leftPiston");
		rightPiston = RobotProvider.instance.getSolenoid("intake.rightPiston");
		ph = new PneumaticHub();

	}

	public void startIntake() {
		checkContextOwnership();
		
		pistonsOut();
		topBelt.set(1.0);
		bottomWheels.set(1.0);

	}

	public void intakePistonless() {
		topBelt.set(1.0);
		bottomWheels.set(1.0);
	}

	public void stopIntake() {
		checkContextOwnership();
		
		topBelt.set(0.0);
		bottomWheels.set(0.0);
		pistonsIn();
	}

	public void pistonsOut() {
		checkContextOwnership();
		log("pistons out");

		leftPiston.set(true);
		rightPiston.set(true);
	}

	public void pistonsIn() {
		checkContextOwnership();

		leftPiston.set(false);
		rightPiston.set(false);
	}

	public void reverseIntake() {
		checkContextOwnership();

		topBelt.set(-1.0);
		bottomWheels.set(-1.0);

		pistonsIn();
	}

	public void run(){
		if(!runRateLimiter.next()) return;

		SmartDashboard.putNumber("Storage PSI",ph.getPressure(0));
	}
}