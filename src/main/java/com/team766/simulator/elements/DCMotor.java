package com.team766.simulator.elements;

import com.team766.simulator.interfaces.ElectricalDevice;
import com.team766.simulator.interfaces.MechanicalAngularDevice;

public class DCMotor implements ElectricalDevice, MechanicalAngularDevice {
	// TODO: Add rotor inertia
	// TODO: Add thermal effects
	
	public static DCMotor makeCIM() {
		return new DCMotor(46.513, 0.018397, 0.091603053435115);
	}
	public static DCMotor make775Pro() {
		return new DCMotor(163.450, 0.0052985, 0.08955223880597);
	}
	
	// Motor velocity constant in radian/second/volt
	// (motor velocity) = kV * (motor voltage) 
	private final double kV;
	
	// Motor torque constant in newton-meter/ampere
	// (motor torque) = kT * (motor current)
	private final double kT;

	// Motor resistance is calculated as 12V / (stall current at 12V)
	private final double motorResistance;
	
	private ElectricalDevice.Output electricalState = new ElectricalDevice.Output(0);
	private MechanicalAngularDevice.Input mechanicalState = new MechanicalAngularDevice.Input(0);

	public DCMotor(double kV, double kT, double motorResistance) {
		this.kV = kV;
		this.kT = kT;
		this.motorResistance = motorResistance;
	}

	@Override
	public MechanicalAngularDevice.Output step(MechanicalAngularDevice.Input input) {
		mechanicalState = new MechanicalAngularDevice.Input(input);
		
		return new MechanicalAngularDevice.Output(kT * electricalState.current);
	}

	@Override
	public ElectricalDevice.Output step(ElectricalDevice.Input input) {
		electricalState = new ElectricalDevice.Output((input.voltage - mechanicalState.angularVelocity / kV) / motorResistance);
		return electricalState;
	}
}
