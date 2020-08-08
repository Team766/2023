package com.team766.simulator;

import java.util.ArrayList;

import com.team766.simulator.interfaces.PneumaticDevice;

public class PneumaticsSystem {
	public static final double PSI_TO_PASCALS = 6894.75729;
	
	private static class BranchCircuit {
		public PneumaticDevice device;
		public double regulatedPressure;
	}
	
	private ArrayList<BranchCircuit> branchCircuits = new ArrayList<BranchCircuit>();
	
	private double systemPressure = Parameters.STARTING_PRESSURE;
	private double compressedAirVolume = 0.0;
	private boolean initialized = false;
	
	public void addDevice(PneumaticDevice device, double regulatedPressure) {
		BranchCircuit circuit = new BranchCircuit();
		circuit.device = device;
		circuit.regulatedPressure = regulatedPressure;
		branchCircuits.add(circuit);
	}
	
	public void step() {
		double flowVolume = 0.0;
		double systemVolume = 0.0;
		for (BranchCircuit circuit : branchCircuits) {
			double devicePressure = Math.min(circuit.regulatedPressure, systemPressure);
			PneumaticDevice.Input inputState = new PneumaticDevice.Input(devicePressure);
			PneumaticDevice.Output deviceState = circuit.device.step(inputState);
			// TODO: implement relieving pressure regulator (make sure device pressure doesn't exceed
			// circuit.regulatedPressure, even when including flow volume)
			flowVolume += deviceState.flowVolume;
			systemVolume += deviceState.deviceVolume;
		}
		compressedAirVolume += flowVolume;
		if (systemVolume == 0.) {
			throw new RuntimeException("Your pneumatics system has no storage volume");
		}
		if (!initialized) {
			compressedAirVolume = systemVolume * (systemPressure + PhysicalConstants.ATMOSPHERIC_PRESSURE) / PhysicalConstants.ATMOSPHERIC_PRESSURE;
			initialized = true;
		}
		systemPressure = compressedAirVolume / systemVolume * PhysicalConstants.ATMOSPHERIC_PRESSURE - PhysicalConstants.ATMOSPHERIC_PRESSURE;
	}
	
	public double getSystemPressure() {
		return systemPressure;
	}
	
	// Simulate the system venting all of its compressed air (e.g. someone opened the release valve;
	// to simulate the pneumatics system becoming compromised, call this method on every simulation tick)
	public void ventPressure() {
		systemPressure = 0;
		initialized = false;
	}
}
