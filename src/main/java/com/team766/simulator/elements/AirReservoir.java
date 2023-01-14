package com.team766.simulator.elements;

import com.team766.simulator.interfaces.PneumaticDevice;

public class AirReservoir implements PneumaticDevice {

	private double volume;
	
	public AirReservoir(double volume) {
		this.volume = volume;
	}
	
	@Override
	public PneumaticDevice.Output step(PneumaticDevice.Input input) {
		return new PneumaticDevice.Output(0, volume);
	}
}
