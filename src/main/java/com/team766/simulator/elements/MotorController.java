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
	public ElectricalDevice.Output step(ElectricalDevice.Input input, double dt) {
		double command = getCommand();
		ElectricalDevice.Input downstreamInput = new ElectricalDevice.Input(input.voltage * command);
		ElectricalDevice.Output downstreamOutput = downstream.step(downstreamInput, dt);
		return new Output(Math.max(0, downstreamOutput.current * Math.signum(command)));
	}

	@Override
	public String name() {
		return "MotorController:" + downstream.name();
	}
}
