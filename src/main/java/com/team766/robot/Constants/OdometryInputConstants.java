package com.team766.robot.constants;

import com.team766.hal.MotorController;

/**
 * Constants used for the Operator Interface, eg for joyticks, buttons, dials, etc.
 * 
 */
public final class OdometryInputConstants {

	public static double WHEEL_CIRCUMFERENCE = 30.5 / 100;
	public static double GEAR_RATIO = 6.75;
	public static int ENCODER_TO_REVOLUTION_CONSTANT = 2048;
	public static double DISTANCE_BETWEEN_WHEELS = 24 * 2.54 / 100;
	public static double RATE_LIMITER_TIME = 0.05;

}
