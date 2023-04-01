package com.team766.robot.mechanisms;

/**
 * Helper classes
 */
public final class ArmsUtil {

	/**
	 * Static class only
	 */
	private ArmsUtil() {
	}

	public static double degreesToEU(double angle) {
		return angle * (44.0 / 90) * (25.0 / 16.0);
	}
	
	public static double EUTodegrees(double EU) {
		return EU * (90 / 44.0) * (16.0 / 25.0);
	}

	public static double AbsToEU(double abs) {
		return degreesToEU(360 * abs);
	}

	public static double EUToAbs(double EU) {
		return EUTodegrees(EU) / 360.0;
	}

	/**
	 * Cosine law
	 * @param side1
	 * @param side2
	 * @param angle in degrees
	 * @return
	 */
	public static double lawOfCosines(double side1, double side2, double angle) {
		double side3Squared = (Math.pow(side1,2.0)+Math.pow(side2,2.0)-2*side1*side2*Math.cos(Math.toRadians(angle)));
		return Math.sqrt(side3Squared);
	}

	public static double lawOfSines(double side1, double angle1, double side2) {
		return Math.asin(side2*Math.sin(angle1)/side1);
	}

	public static double clampValueToRange(double value, double max, double min) {
		if(value > max){ 
			value = max;
		} else if( value < min){
			value = min;
		}
		return value;
	}
}
