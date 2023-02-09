package com.team766.simulator.elements;

import com.team766.simulator.interfaces.ElectricalDevice;
import com.team766.simulator.interfaces.MechanicalAngularDevice;

public class DCMotor implements ElectricalDevice, MechanicalAngularDevice {
	// TODO: Add rotor inertia
	// TODO: Add thermal effects
	
	// Motor data from https://motors.vex.com/
	public static DCMotor makeCIM(String name) {
		return new DCMotor(name, 12, 5330, 2.7, 2.41, 131);
	}
	public static DCMotor make775Pro(String name) {
		return new DCMotor(name, 12, 18730, 0.7, 0.71, 134);
	}
	public static DCMotor makeFalcon500(String name) {
		return new DCMotor(name, 12, 6380, 1.5, 4.69, 257);
	}
	public static DCMotor makeNeo(String name) {
		return new DCMotor(name, 12, 5880, 1.3, 3.36, 166);
	}
	public static DCMotor makeNeo550(String name) {
		return new DCMotor(name, 12, 11710, 1.1, 1.08, 111);
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

	private final String m_name;

	public DCMotor(String name, double referenceVoltage, double freeSpeedRpm, double freeCurrentAmps, double stallTorqueNewtonMeters, double stallCurrentAmps) {
		m_name = name;

		this.motorResistance = referenceVoltage / stallCurrentAmps;
		this.kV = freeSpeedRpm / 60.0 * 2 * Math.PI / (referenceVoltage - motorResistance * freeCurrentAmps);
		this.kT = stallTorqueNewtonMeters / stallCurrentAmps;
	}

	@Override
	public MechanicalAngularDevice.Output step(MechanicalAngularDevice.Input input, double dt) {
		mechanicalState = input;
		
		return new MechanicalAngularDevice.Output(kT * electricalState.current);
	}

	@Override
	public ElectricalDevice.Output step(ElectricalDevice.Input input, double dt) {
		electricalState = new ElectricalDevice.Output((input.voltage - mechanicalState.angularVelocity / kV) / motorResistance);
		return electricalState;
	}

	@Override
	public String name() {
		return m_name;
	}
}
