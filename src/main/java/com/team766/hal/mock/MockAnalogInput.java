package com.team766.hal.mock;

import com.team766.hal.AnalogInputReader;

public class MockAnalogInput implements AnalogInputReader {
	
	private double sensor = 0.0;
	
	public void set(double value){
		sensor = value;
	}

	@Override
	public double getVoltage() {
		return sensor;
	}

}
