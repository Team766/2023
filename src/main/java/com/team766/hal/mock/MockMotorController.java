package com.team766.hal.mock;

import com.team766.hal.BasicMotorController;

public class MockMotorController implements BasicMotorController {

	private double output;
	
	public MockMotorController(int index) {
		output = 0;
	}
	
	@Override
	public double get() {
		return output;
	}
	
	@Override
	public void set(double power) {
		output = power;
	}

	@Override
	public void restoreFactoryDefault() {
		// No-op
	}

}
