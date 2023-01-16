package com.team766.hal.mock;

import com.team766.hal.BeaconReader;

public class MockBeaconSensor implements BeaconReader {

	@Override
	public BeaconPose[] getBeacons() {
		return new BeaconPose[0];
	}

}
