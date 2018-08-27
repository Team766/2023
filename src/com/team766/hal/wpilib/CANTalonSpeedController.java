package com.team766.hal.wpilib;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.team766.hal.CANSpeedController;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public class CANTalonSpeedController extends WPI_TalonSRX implements CANSpeedController {

	public CANTalonSpeedController(int deviceNumber) {
		super(deviceNumber);
	}

	@Override
	public void set(ControlMode mode, double value) {
		com.ctre.phoenix.motorcontrol.ControlMode ctre_mode = null;
		switch (mode) {
		case PercentOutput:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;
			break;
		case Position:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.Position;
			break;
		case Velocity:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.Velocity;
			break;
		case Current:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.Current;
			break;
		case Follower:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.Follower;
			break;
		case MotionProfile:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.MotionProfile;
			break;
		case MotionMagic:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.MotionMagic;
			break;
		case MotionProfileArc:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.MotionProfileArc;
			break;
		case Disabled:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.Disabled;
			break;
		}
		if (ctre_mode == null) {
			Logger.get(Category.HAL).log(
					Severity.ERROR,
					"CAN ControlMode is not translatable: " + mode);
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.Disabled;
		}
		super.set(ctre_mode, value);
	}

	@Override
	public void stopMotor() {
		super.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, 0);
	}

	@Override
	public double getSensorPosition() {
		return super.getSelectedSensorPosition(0);
	}

	@Override
	public double getSensorVelocity() {
		return super.getSelectedSensorVelocity(0);
	}
	
	@Override
	public void setPosition(int position){
		super.setSelectedSensorPosition(position, 0, 20);
	}
	
}
