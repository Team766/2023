package com.team766.simulator.elements;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.team766.simulator.PhysicalConstants;
import com.team766.simulator.interfaces.MechanicalDevice;
import com.team766.simulator.interfaces.PneumaticDevice;

public class DoubleActingPneumaticCylinder implements PneumaticDevice, MechanicalDevice {
	private static final Vector3D FORWARD = new Vector3D(1, 0, 0);
	
	private final double boreDiameter;
	private final double stroke;
	
	private boolean isExtended = false;
	private boolean commandExtended = false;
	
	private PneumaticDevice.Input pneumaticState = new PneumaticDevice.Input(0);
	
	public DoubleActingPneumaticCylinder(double boreDiameter, double stroke) {
		this.boreDiameter = boreDiameter;
		this.stroke = stroke;
	}
	
	public void setExtended(boolean state) {
		commandExtended = state;
	}
	
	@Override
	public PneumaticDevice.Output step(PneumaticDevice.Input input) {
		pneumaticState = input;
		PneumaticDevice.Output output;
		double deviceVolume = boreArea() * stroke;
		if (commandExtended != isExtended) {
			output = new PneumaticDevice.Output(-deviceVolume * (input.pressure + PhysicalConstants.ATMOSPHERIC_PRESSURE) / PhysicalConstants.ATMOSPHERIC_PRESSURE, deviceVolume); 
		} else {
			output = new PneumaticDevice.Output(0, deviceVolume);
		}
		isExtended = commandExtended;
		return output;
	}

	@Override
	public MechanicalDevice.Output step(MechanicalDevice.Input input) {
		Vector3D direction = isExtended ? FORWARD : FORWARD.negate();
		return new MechanicalDevice.Output(direction.scalarMultiply(boreArea() * pneumaticState.pressure));
	}

	private double boreArea() {
		return Math.PI * Math.pow(boreDiameter / 2., 2);
	}
}
