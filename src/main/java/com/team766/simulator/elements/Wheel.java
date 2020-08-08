package com.team766.simulator.elements;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.team766.simulator.Parameters;
import com.team766.simulator.PhysicalConstants;
import com.team766.simulator.interfaces.MechanicalAngularDevice;
import com.team766.simulator.interfaces.MechanicalDevice;

// Simulate a wheel revolving around the positive Y axis.
public class Wheel implements MechanicalDevice {
	// TODO: Add transverse friction
	// TODO: Add traction limit/wheel slip (static vs kinetic friction)
	// TODO: Add rotational inertia
	
	private static final Vector3D FORWARD = new Vector3D(-1, 0, 0);
	
	// Diameter of the wheel in meters
	private final double diameter;
	
	private MechanicalAngularDevice upstream;
	
	public Wheel(double diameter, MechanicalAngularDevice upstream) {
		this.diameter = diameter;
		this.upstream = upstream;
	}

	@Override
	public MechanicalDevice.Output step(MechanicalDevice.Input input) {
		MechanicalAngularDevice.Input upstreamInput =
			new MechanicalAngularDevice.Input(FORWARD.dotProduct(input.velocity) * 2. / diameter);
		MechanicalAngularDevice.Output upstreamOutput = upstream.step(upstreamInput);
		double appliedForce = upstreamOutput.torque * 2. / diameter;
		double maxFriction = Parameters.WHEEL_COEFFICIENT_OF_FRICTION * Parameters.ROBOT_MASS * PhysicalConstants.GRAVITY_ACCELERATION / Parameters.NUM_LOADED_WHEELS;
		if (Math.abs(appliedForce) > maxFriction) {
			appliedForce = Math.signum(appliedForce) * maxFriction;
		}
		return new MechanicalDevice.Output(FORWARD.scalarMultiply(appliedForce));
	}
}
