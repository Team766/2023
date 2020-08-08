package com.team766.simulator.interfaces;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public interface MechanicalDevice {
	public class Input {
		public Input(Vector3D position, Vector3D velocity) {
			this.position = position;
			this.velocity = velocity;
		}
		public Input(Input other) {
			position = other.position;
			velocity = other.velocity;
		}
		
		public final Vector3D position;
		public final Vector3D velocity;
	}
	
	public class Output {
		public Output(Vector3D force) {
			this.force = force;
		}
		public Output(Output other) {
			force = other.force;
		}
		
		public final Vector3D force;
	}
	
	public Output step(Input input);
}
