package com.team766.robot.constants;

/**
 * Constants used for the Operator Interface, eg for joyticks, buttons, dials, etc.
 */
public final class InputConstants {

	//Joysticks
	public static final int LEFT_JOYSTICK = 0;
	public static final int RIGHT_JOYSTICK = 1;
	public static final int BOXOP_GAMEPAD = 2;

	//Navigation
	public static final int AXIS_LEFT_RIGHT = 0;
	public static final int AXIS_FORWARD_BACKWARD = 1;
	public static final int AXIS_TWIST = 3;

	// Joystick buttons
	public static final int INTAKE_OUT = 1;
	public static final int FINE_DRIVING = 1;
	public static final int CROSS_WHEELS = 3;
	public static final int RESET_GYRO = 9;
	public static final int RESET_POS = 15;

	// Boxop Gamepad Buttons
	// LB/RB
	public static final int BUTTON_INTAKE_IN = 1;
	public static final int BUTTON_HUMAN_PLAYER = 5;

	// RT
	public static final int BUTTON_EXTEND_WRISTVATOR = 6;

	// left axis
	public static final int AXIS_WRIST_MOVEMENT = 4;
	// right axis
	public static final int AXIS_ELEVATOR_MOVEMENT = 5;
	// dpad
	public static final int AXIS_CONECUB_SELECT = 6;

	// X/A/B/Y
	public static final int BUTTON_PLACEMENT_HIGH = 2; // Y
	public static final int BUTTON_PLACEMENT_MID = 3;  // B
	public static final int BUTTON_PLACEMENT_LOW = 4;  // A

}