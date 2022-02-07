package com.team766.math;

public class Math {
	public static double clamp(double x, double min, double max) {
		if (x < min) return min;
		if (x > max) return max;
		return x;
	}

	/**
	 * Returns the given angle, normalized to be within the range [-180, 180)
	 */
	public static double normalizeAngleDegrees(double angle) {
		while (angle < -180) {
			angle += 360;
		}
		while (angle >= 180) {
			angle -= 360;
		}
		return angle;
	}
}
