package com.team766.simulator.elements;

import com.team766.simulator.ProgramInterface;
import com.team766.simulator.interfaces.ElectricalDevice;

public class CanSpeedController extends SpeedController {

	private int address;
	
	public CanSpeedController(int address, ElectricalDevice downstream) {
		super(downstream);
		
		this.address = address;
	}

	@Override
	protected double getCommand() {
		return ProgramInterface.canSpeedControllerChannels[address].command.output;
	}

}
