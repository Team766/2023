package com.team766.simulator.elements;

import com.team766.simulator.ProgramInterface;
import com.team766.simulator.interfaces.ElectricalDevice;

public class PwmMotorController extends MotorController {
	
	private int channel;

	public PwmMotorController(int channel, ElectricalDevice downstream) {
		super(downstream);
		
		this.channel = channel;
	}

	@Override
	protected double getCommand() {
		return ProgramInterface.pwmChannels[channel];
	}

}
