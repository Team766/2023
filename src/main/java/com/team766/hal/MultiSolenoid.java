package com.team766.hal;

public class MultiSolenoid implements SolenoidController {
	
	private SolenoidController[] solenoids;
	private boolean state;
	
	public MultiSolenoid(SolenoidController... solenoids) {
		this.solenoids = solenoids;

		set(false);
	}
	
	@Override
	public boolean get() {
		return state;
	}
	
	@Override
	public void set(boolean on) {
		state = on;
		for (SolenoidController s : solenoids) {
			s.set(on);
		}
	}

}