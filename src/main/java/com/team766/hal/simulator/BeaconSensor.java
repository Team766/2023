package com.team766.hal.simulator;

import com.team766.hal.BeaconReader;
import com.team766.simulator.ProgramInterface;

public class BeaconSensor implements BeaconReader {

	@Override
	public BeaconPose[] getBeacons() {
		return ProgramInterface.beacons;
	}

}
