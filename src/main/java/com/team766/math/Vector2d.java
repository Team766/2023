package com.team766.math;

public class Vector2d implements Algebraic<Vector2d> {
	public static final Vector2d ZERO = new Vector2d(0, 0);
	public static final Vector2d UNIT_X = new Vector2d(1, 0);
	public static final Vector2d UNIT_Y = new Vector2d(0, 1);
	
	public final double x;
	public final double y;
	
	public Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2d(Vector2d other) {
		this.x = other.x;
		this.y = other.y;
	}

	public double magnitude() {
		return java.lang.Math.sqrt(x * x + y * y);
	}

	public double magnitudeSq() {
		return x * x + y * y;
	}

	public double angle() {
		return java.lang.Math.atan2(y, x);
	}
	
	public Vector2d add(Vector2d b) {
		return new Vector2d(x + b.x, y + b.y);
	}
	
	public Vector2d subtract(Vector2d b) {
		return new Vector2d(x - b.x, y - b.y);
	}
	
	public double dot(Vector2d b) {
		return x * b.x + y * b.y;
	}
	
	public Vector2d scale(double b) {
		return new Vector2d(x * b, y * b);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Vector2d)) {
			return false;
		}
		Vector2d otherVector = (Vector2d)other;
		return x == otherVector.x && y == otherVector.y;
	}
	
	@Override
	public String toString() {
		return String.format("[%s, %s]", x, y);
	}
}
