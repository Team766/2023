package com.team766.hal;

public interface BeaconReader {
	public static class BeaconPose {
		public double x;
		public double y;
		public double z;
		public double yaw;
		public double pitch;
		public double roll;
	}

	public abstract BeaconPose[] getBeacons();
}
