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
	public static final int FINE_DRIVING = 1;

	// Control Panel Buttons
	public static final int CONE_HIGH = 1;
	public static final int CONE_MID = 2;
	public static final int HUMANPLAYER_PICKUP = 3;
	public static final int INTAKE = 5;
	public static final int UNSTOWED = 6;
	public static final int ARM_READY = 7;
	public static final int IN_CHASSIS = 8;
	public static final int INTAKE_PISTONLESS = 9;
	public static final int OUTTAKE = 10;

	public static final int RESET_GYRO = 11;
	public static final int RESET_POS = 12;

	public static final int ANTI_GRAV = 16;


	public enum IntakeState {
		IDLE,
		SPINNINGREV,
		SPINNINGFWD
	}

	

}