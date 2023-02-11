package com.team766.simulator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import com.team766.simulator.interfaces.ElectricalDevice;

public class ElectricalSystem {
	private double nominalVoltage = Parameters.BATTERY_VOLTAGE;
	private double primaryResistance = Parameters.PRIMARY_ELECTRICAL_RESISTANCE;

	public class BranchInfo {
		public final ElectricalDevice device;
		public ElectricalDevice.Output flow;

		public BranchInfo(ElectricalDevice device) {
			this.device = device;
			this.flow = new ElectricalDevice.Output(0);
		}
	}
	
	private ArrayList<BranchInfo> branchCircuits = new ArrayList<BranchInfo>();
	
	private ElectricalDevice.Input systemState;
	
	public ElectricalSystem() {
		systemState = new ElectricalDevice.Input(nominalVoltage);
	}
	
	public void addDevice(ElectricalDevice device) {
		branchCircuits.add(new BranchInfo(device));
	}
	
	public void step(double dt) {
		double current = 0.0;
		for (BranchInfo branch : branchCircuits) {
			branch.flow = branch.device.step(systemState, dt);
			current += branch.flow.current;
		}
		systemState = new ElectricalDevice.Input(Math.max(0, nominalVoltage - primaryResistance * current));
	}
	
	public double getSystemVoltage() {
		return systemState.voltage;
	}

	public LinkedHashMap<String, Double> getBranchCurrents() {
		var currents = new LinkedHashMap<String, Double>();
		for (BranchInfo branch : branchCircuits) {
			currents.put(branch.device.name(), branch.flow.current);
		}
		return currents;
	}
}
