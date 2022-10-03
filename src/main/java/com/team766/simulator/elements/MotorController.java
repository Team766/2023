package com.team766.simulator.elements;

import com.team766.simulator.interfaces.ElectricalDevice;

public abstract class MotorController implements ElectricalDevice {
	private ElectricalDevice downstream;
	
	public MotorController(ElectricalDevice downstream) {
		this.downstream = downstream;
	}
	
	// [-1, 1] representing the command sent from the application processor
	protected abstract double getCommand();
	
	@Override
	public ElectricalDevice.Output step(ElectricalDevice.Input input) {
		double command = getCommand();
		ElectricalDevice.Input downstreamInput = new ElectricalDevice.Input(input.voltage * command);
		ElectricalDevice.Output downstreamOutput = downstream.step(downstreamInput);
		return new Output(downstreamOutput.current * Math.signum(command));
	}
}
