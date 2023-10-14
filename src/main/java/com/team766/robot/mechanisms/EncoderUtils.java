package com.team766.robot.mechanisms;

/**
 * Helper classes
 */
public final class EncoderUtils {

	/**
	 * Static class only
	 */
	private EncoderUtils() {
	}

	public static double wristDegreesToEU(double angle) {
		// (chain reduction) * (planetary reduction) * (degrees to rotations)
		return (33. / 10.) * (75./1.) * (1./360.) * angle;
	}
	
	public static double wristEUTodegrees(double EU) {
		// (chain reduction) * (planetary reduction) * (rotations to degrees)
		return (10. / 33.) * (1./75.) * (360./1.) * EU;
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

	public static double clampValueToRange(double value, double min, double max) {
		if(value > max){ 
			value = max;
		} else if( value < min){
			value = min;
		}
		return value;
	}
}
