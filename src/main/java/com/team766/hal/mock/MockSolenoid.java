package com.team766.hal.mock;

import com.team766.hal.SolenoidController;

public class MockSolenoid implements SolenoidController{

	private boolean pist;
	
	public MockSolenoid(int port){
		pist = false;
	}
	
	public void set(boolean on) {
		pist = on;
	}

	public boolean get() {
		return pist;
	}

}
