package com.team766.hal.mock;

import com.team766.hal.DigitalInputReader;

public class MockDigitalInput implements DigitalInputReader{
	
	private boolean sensor = false;

	public boolean get() {
		return sensor;
	}
	
	public void set(boolean on){
		sensor = on;
	}

}
