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
	public static final int GRABBER_RELEASE = 1;
	public static final int FINE_DRIVING = 1;
	public static final int CROSS_WHEELS = 3;
	public static final int RESET_GYRO = 9;
	public static final int RESET_POS = 15;

	// Control Panel Buttons
	public static final int CONE_HIGH = 1;
	public static final int ARM_READY = 2;
	public static final int UNSTOWED = 3;
	public static final int NUDGE_UP = 4;
	public static final int GRAB_IN = 5;
	public static final int CONE_MID = 6;
	public static final int HUMANPLAYER_PICKUP = 7;
	public static final int NUDGE_DOWN = 9;
	//public static final int GRAB_OUT = 10;   Function replaced by joystick trigger
	public static final int INTAKE = 11;
	public static final int OUTTAKE = 12;
	public static final int ARM_STOP = 14;
	public static final int BRAKE = 15;
	public static final int COAST = 16;


	//public static final int ANTI_GRAV = 13;


	public enum IntakeState {
		IDLE,
		SPINNINGREV,
		SPINNINGFWD
	}

	

}