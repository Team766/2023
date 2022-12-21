package com.team766.math;

public class Rotation2d {
	public final double angle;
	private final double cosAngle;
	private final double sinAngle;

	public Rotation2d(double angle) {
		this.angle = angle;
		this.cosAngle = java.lang.Math.cos(angle);
		this.sinAngle = java.lang.Math.sin(angle);
	}

	public Vector2d transform(Vector2d in) {
		return new Vector2d(
			cosAngle * in.x - sinAngle * in.y,
			sinAngle * in.x + cosAngle * in.y
		);
	}
}
