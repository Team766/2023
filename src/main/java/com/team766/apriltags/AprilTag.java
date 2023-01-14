package com.team766.apriltags;

public class AprilTag {
	private final int id;
	private final double x;
	private final double y;
	private final double z;
	private final int rotation;

	public AprilTag (int id, double x, double y, double z, int rotation) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotation = rotation;
	}

	public int getId() {
		return id;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public int getRotation() {
		return rotation;
	}
}
