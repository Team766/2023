package com.team766.simulator.interfaces;

public interface MechanicalAngularDevice {
	public class Input {
		public Input(double angularVelocity) {
			this.angularVelocity = angularVelocity;
		}
		public Input(Input other) {
			this.angularVelocity = other.angularVelocity; 
		}

		public final double angularVelocity;
	}
	
	public class Output {
		public Output(double torque) {
			this.torque = torque;
		}
		public Output(Output other) {
			this.torque = other.torque;
		}
		
		public final double torque;
	}
	
	public Output step(Input input);
}
