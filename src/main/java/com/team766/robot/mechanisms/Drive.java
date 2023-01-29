package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com. team766.hal.RobotProvider;

public class Drive extends Mechanism {
	private MotorController leftMotor;
	private MotorController rightMotor;

	public Drive() {
		leftMotor = RobotProvider.instance.getMotor("drive.leftMotor");
		rightMotor = RobotProvider.instance.getMotor("drive.rightMotor");
	}

	public void setDrivePower(double leftPower, double rightPower) {
		checkContextOwnership();
		log(leftPower + "  " + rightPower);
		leftMotor.set(leftPower);
		rightMotor.set(rightPower);
	}
	public void setArcadeDrivePower(double forward, double turn) {
		checkContextOwnership();

		double leftMotorPower = turn + forward;
		double rightMotorPower = -turn + forward;
		if(Math.max(leftMotorPower,rightMotorPower) >1){
			rightMotorPower /= Math.max(leftMotorPower,rightMotorPower);
			leftMotorPower /= Math.max(leftMotorPower,rightMotorPower);
		}
		if(Math.min(leftMotorPower,rightMotorPower) <-1){
			rightMotorPower /= Math.abs(Math.min(leftMotorPower,rightMotorPower));
			leftMotorPower /= Math.abs(Math.min(leftMotorPower,rightMotorPower));
		}
		setDrivePower(leftMotorPower, rightMotorPower);
	}

}