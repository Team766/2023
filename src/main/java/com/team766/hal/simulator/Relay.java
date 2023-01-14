package com.team766.hal.simulator;

import com.team766.hal.RelayOutput;
import com.team766.simulator.ProgramInterface;

public class Relay implements RelayOutput{

	private int channel;
	
	public Relay(int channel){
		this.channel = channel;
	}
	
	@Override
	public void set(Value out) {
		switch(out) {
		case kForward:
			ProgramInterface.relayChannels[channel] = 1;
			break;
		case kOff:
			ProgramInterface.relayChannels[channel] = 0;
			break;
		case kOn:
			ProgramInterface.relayChannels[channel] = 1;
			break;
		case kReverse:
			ProgramInterface.relayChannels[channel] = -1;
			break;
		}
	}
	
}
