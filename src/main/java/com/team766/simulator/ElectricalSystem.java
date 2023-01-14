package com.team766.simulator;

import java.util.ArrayList;

import com.team766.simulator.interfaces.ElectricalDevice;

public class ElectricalSystem {
	private double nominalVoltage = Parameters.BATTERY_VOLTAGE;
	private double primaryResistance = Parameters.PRIMARY_ELECTRICAL_RESISTANCE;
	
	private ArrayList<ElectricalDevice> branchCircuits = new ArrayList<ElectricalDevice>();
	
	private ElectricalDevice.Input systemState;
	
	public ElectricalSystem() {
		systemState = new ElectricalDevice.Input(nominalVoltage);
	}
	
	public void addDevice(ElectricalDevice device) {
		branchCircuits.add(device);
	}
	
	public void step() {
		double current = 0.0;
		for (ElectricalDevice device : branchCircuits) {
			ElectricalDevice.Output deviceState = device.step(systemState);
			current += deviceState.current;
		}
		systemState = new ElectricalDevice.Input(Math.max(0, nominalVoltage - primaryResistance * current));
	}
	
	public double getSystemVoltage() {
		return systemState.voltage;
	}
}
