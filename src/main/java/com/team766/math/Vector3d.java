package com.team766.math;

public class Vector3d implements Algebraic<Vector3d> {
	public static final Vector3d ZERO = new Vector3d(0, 0, 0);
	public static final Vector3d UNIT_X = new Vector3d(1, 0, 0);
	public static final Vector3d UNIT_Y = new Vector3d(0, 1, 0);
	public static final Vector3d UNIT_Z = new Vector3d(0, 0, 1);
	
	public final double x;
	public final double y;
	public final double z;
	
	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3d(Vector3d other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}

	public double magnitude() {
		return java.lang.Math.sqrt(x * x + y * y + z * z);
	}

	public double magnitudeSq() {
		return x * x + y * y + z * z;
	}

	public Vector2d xy() {
		return new Vector2d(x, y);
	}
	
	public Vector3d add(Vector3d b) {
		return new Vector3d(x + b.x, y + b.y, z + b.z);
	}
	
	public Vector3d subtract(Vector3d b) {
		return new Vector3d(x - b.x, y - b.y, z - b.z);
	}
	
	public double dot(Vector3d b) {
		return x * b.x + y * b.y + z * b.z;
	}
	
	public Vector3d cross(Vector3d b) {
		return new Vector3d(y * b.z - z * b.y,
		                   z * b.x - x * b.z,
		                   x * b.y - y * b.x);
	}
	
	public Vector3d scale(double b) {
		return new Vector3d(x * b, y * b, z * b);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Vector3d)) {
			return false;
		}
		Vector3d otherVector = (Vector3d)other;
		return x == otherVector.x && y == otherVector.y && z == otherVector.z;
	}
	
	@Override
	public String toString() {
		return String.format("[%s, %s, %s]", x, y, z);
	}
}
