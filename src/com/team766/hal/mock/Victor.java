package com.team766.hal.mock;

import com.team766.hal.SpeedController;

public class Victor implements SpeedController{

	private double output;
	
	public Victor(int index){
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
	
}
