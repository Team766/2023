package com.team766.simulator.elements;

import com.team766.simulator.interfaces.MechanicalAngularDevice;

public class Gears implements MechanicalAngularDevice {
	// TODO: Add rotational inertia
	// TODO: Add losses

	// Torque ratio (output / input)
	private final double torqueRatio;
	
	private MechanicalAngularDevice upstream;
	
	public Gears(double torqueRatio, MechanicalAngularDevice upstream) {
		this.torqueRatio = torqueRatio;
		this.upstream = upstream;
	}

	@Override
	public MechanicalAngularDevice.Output step(MechanicalAngularDevice.Input input) {
		MechanicalAngularDevice.Input upstreamInput =
			new MechanicalAngularDevice.Input(input.angularVelocity * torqueRatio);
		MechanicalAngularDevice.Output upstreamOutput = upstream.step(upstreamInput);
		return new MechanicalAngularDevice.Output(upstreamOutput.torque * torqueRatio);
	}
}
