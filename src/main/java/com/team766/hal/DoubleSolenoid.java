package com.team766.hal;

public class DoubleSolenoid {
	
	SolenoidController forward; 
	SolenoidController back; 
	
	public enum State {
		Forward, Neutral, Backward
	}
	
	public DoubleSolenoid(SolenoidController forward, SolenoidController back) {
		this.forward = forward;
		this.back = back;
	}
	
	public boolean get() {
		return forward.get();
	}
	
	public void set(State state) {
		switch(state) {
			case Forward:
				forward.set(true);
				back.set(false);
				break;
			case Backward:
				forward.set(false);
				back.set(true);
				break;
			case Neutral:
				forward.set(false);
				back.set(false);
				break;
		}
	}
	

}
