package com.team766.hal.simulator;

import com.team766.hal.AnalogInputReader;
import com.team766.simulator.ProgramInterface;

public class AnalogInput implements AnalogInputReader{
	
	private final int channel;
	
	public AnalogInput(int channel) {
		this.channel = channel;
	}
	
	@Override
	public double getVoltage() {
		return ProgramInterface.analogChannels[channel];
	}

}
