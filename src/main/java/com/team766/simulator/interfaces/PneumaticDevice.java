package com.team766.simulator.interfaces;

public interface PneumaticDevice {
	public class Input {
		public Input(double pressure) {
			this.pressure = pressure;
		}
		public Input(Input other) {
			pressure = other.pressure;
		}
		
		// Pascals (relative pressure)
		public final double pressure;
	}
	
	public class Output {
		public Output(double flowVolume, double deviceVolume) {
			this.flowVolume = flowVolume;
			this.deviceVolume = deviceVolume;
		}
		public Output(Output other) {
			flowVolume = other.flowVolume;
			deviceVolume = other.deviceVolume;
		}
		
		// Volumetric flow (delta m^3 at atmospheric pressure)
		// Positive flow is into the system, e.g. from a compressor
		// Negative flow is out of the system, e.g. from venting to atmosphere
		public final double flowVolume;
		
		// Volume of air that the device contains (m^3)
		public final double deviceVolume;
		
		// Note that an expanding volume (such as a cylinder expanding)
		// should increase volume, but have 0 flow volume because no
		// pressurized air is actually leaving the system.
	}
	
	public Output step(Input input);
}
