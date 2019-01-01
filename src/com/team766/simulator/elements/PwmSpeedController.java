package com.team766.simulator.elements;

import com.team766.simulator.ProgramInterface;
import com.team766.simulator.interfaces.ElectricalDevice;

public class PwmSpeedController extends SpeedController {
	
	private int channel;

	public PwmSpeedController(int channel, ElectricalDevice downstream) {
		super(downstream);
		
		this.channel = channel;
	}

	@Override
	protected double getCommand() {
		return ProgramInterface.pwmChannels[channel];
	}

}
