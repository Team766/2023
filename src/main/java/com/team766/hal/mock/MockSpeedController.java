package com.team766.hal.mock;

import com.team766.hal.BasicSpeedController;

public class MockSpeedController implements BasicSpeedController {

	private double output;
	
	public MockSpeedController(int index) {
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
	public void restoreFactoryDefault() {
		// No-op
	}

}
