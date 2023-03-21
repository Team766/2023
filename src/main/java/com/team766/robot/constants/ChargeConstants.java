package com.team766.robot.constants;

/**
 * Constants used for the Operator Interface, eg for joyticks, buttons, dials, etc.
 * 
 * TODO: consider moving this into a config file.
 */
public final class ChargeConstants {

	public static final double BLUE_BALANCE_TARGET_X = 3.9417625;
	public static final double BLUE_BALANCE_LEFT_EDGE = 2.974975;
	public static final double BLUE_BALANCE_RIGHT_EDGE = 4.90855;

	public static final double RED_BALANCE_TARGET_X = 12.5999875;
	public static final double RED_BALANCE_LEFT_EDGE = 11.6332;
	public static final double RED_BALANCE_RIGHT_EDGE = 13.566775;

	public static final double X_ALIGNMENT_THRESHOLD = 0.5;
	public static final double Y_ALIGNMENT_THRESHOLD = 0.5;

	public static final double BLUE_LEFT_PT = BLUE_BALANCE_LEFT_EDGE - X_ALIGNMENT_THRESHOLD;
	public static final double BLUE_RIGHT_PT = BLUE_BALANCE_RIGHT_EDGE + X_ALIGNMENT_THRESHOLD;
	public static final double RED_LEFT_PT = RED_BALANCE_LEFT_EDGE - X_ALIGNMENT_THRESHOLD;
	public static final double RED_RIGHT_PT = RED_BALANCE_RIGHT_EDGE + X_ALIGNMENT_THRESHOLD;

	public static final double CHARGE_TOP_EDGE = 3.96875;
	public static final double CHARGE_BOTTOM_EDGE = 1.4986;
	public static final double MIDDLE = 2.733675;
}