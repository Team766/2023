package com.team766.hal.simulator;

import com.team766.hal.SolenoidController;
import com.team766.simulator.ProgramInterface;

public class Solenoid implements SolenoidController{

	private int channel;
	
	public Solenoid(int channel){
		this.channel = channel;
	}
	
	public void set(boolean on) {
		ProgramInterface.solenoidChannels[channel] = on;
	}

	public boolean get() {
		return ProgramInterface.solenoidChannels[channel];
	}

}
