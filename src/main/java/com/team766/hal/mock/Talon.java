package com.team766.hal.mock;

import com.team766.hal.CANSpeedController;

public class Talon implements CANSpeedController{

	private double output;
	private double sensorPosition;
	private double sensorVelocity;
	private ControlMode controlMode;
	
	public Talon(int index){
		output = 0;
	}
	
	@Override
	public double get() {
		return output;
	}
	
	@Override
	public void set(double speed) {
		output = speed;
	}

	@Override
	public void setInverted(boolean isInverted) {
	}

	@Override
	public boolean getInverted() {
		return false;
	}

	@Override
	public void stopMotor() {
		output = 0;
	}

	public void setSensorPosition(double position) {
		sensorPosition = position;
	}
	
	@Override
	public double getSensorPosition() {
		return sensorPosition;
	}
	
	public void setSensorVelocity(double velocity) {
		sensorVelocity = velocity;
	}

	@Override
	public double getSensorVelocity() {
		return sensorVelocity;
	}

	@Override
	public void set(ControlMode mode, double value) {
		output = value;
		controlMode = mode;
	}
	
	public ControlMode getControlMode() {
		return controlMode;
	}

	@Override
	public void setPosition(int position) {
		sensorPosition = position;
	}
	
}
