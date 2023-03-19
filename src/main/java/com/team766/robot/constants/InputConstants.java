package com.team766.robot.constants;

/**
 * Constants used for the Operator Interface, eg for joyticks, buttons, dials, etc.
 * 
 * TODO: consider moving this into a config file.
 */
public final class InputConstants {

	//Joysticks
	public static final int LEFT_JOYSTICK = 0;
	public static final int RIGHT_JOYSTICK = 1;
	public static final int CONTROL_PANEL = 2;

	//Navigation
	public static final int AXIS_LEFT_RIGHT = 0;
	public static final int AXIS_FORWARD_BACKWARD = 1;
	public static final int AXIS_TWIST = 3;

	// Joystick buttons
	public static final int CROSS_WHEELS = 1;
	public static final int GRABBER_RELEASE = 1;
	public static final int FINE_DRIVING = 2;

	public static final int RESET_GYRO = 4;
	public static final int RESET_POS = 3;

	// Control Panel Buttons
	public static final int CONE_HIGH = 1;
	public static final int CONE_MID = 2;
	public static final int CONE_HYBRID = 3;
	public static final int INTAKE = 5;
	public static final int CUBE_HIGH = 6;
	public static final int CUBE_MID = 7;
	public static final int CUBE_HYBRID = 8;
	public static final int OUTTAKE = 10;

	public static final int ARM_PICKUP_POS = 11;
	public static final int READY_POS = 12;

	public static final int ANTI_GRAV = 16;

	

}