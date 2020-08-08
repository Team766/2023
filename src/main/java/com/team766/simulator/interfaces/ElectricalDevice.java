package com.team766.simulator.interfaces;

public interface ElectricalDevice {
	public class Input {
		public Input(double voltage) {
			this.voltage = voltage;
		}
		public Input(Input other) {
			voltage = other.voltage;
		}
		
		public final double voltage;
	}
	
	public class Output {
		public Output(double current) {
			this.current = current;
		}
		public Output(Output other) {
			current = other.current;
		}
		
		public final double current;
	}
	
	public Output step(Input input);
}
