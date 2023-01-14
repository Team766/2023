package com.team766.hal;

public class DoubleSolenoid implements SolenoidController {
	
	private SolenoidController forward;
	private SolenoidController back;
	private boolean boolState;
	
	public enum State {
		Forward, Neutral, Backward
	}
	
	public DoubleSolenoid(SolenoidController forward, SolenoidController back) {
		this.forward = forward;
		this.back = back;

		set(State.Neutral);
	}
	
	@Override
	public boolean get() {
		return boolState;
	}
	
	public void set(State state) {
		switch(state) {
			case Forward:
				boolState = true;
				if (forward != null) {
					forward.set(true);
				}
				if (back != null) {
					back.set(false);
				}
				break;
			case Backward:
				boolState = false;
				if (forward != null) {
					forward.set(false);
				}
				if (back != null) {
					back.set(true);
				}
				break;
			case Neutral:
				boolState = false;
				if (forward != null) {
					forward.set(false);
				}
				if (back != null) {
					back.set(false);
				}
				break;
		}
	}
	
	@Override
	public void set(boolean on) {
		set(on ? State.Forward : State.Backward);
	}

}
