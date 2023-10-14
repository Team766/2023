package com.team766.robot.constants;

/** Constants used for reading values from the config file. */
public final class ConfigConstants {
	// utility class
	private ConfigConstants() {}

	// wrist config values
	public static final String WRIST_MOTOR = "wrist.motor";
	public static final String WRIST_PGAIN = "wrist.pGain";
	public static final String WRIST_IGAIN = "wrist.iGain";
	public static final String WRIST_DGAIN = "wrist.dGain";
	public static final String WRIST_FFGAIN = "wrist.ffGain";

	// elevator config values
	public static final String ELEVATOR_LEFT_MOTOR = "elevator.leftMotor";
	public static final String ELEVATOR_RIGHT_MOTOR = "elevator.rightMotor";
	public static final String ELEVATOR_PGAIN = "elevator.pGain";
	public static final String ELEVATOR_IGAIN = "elevator.iGain";
	public static final String ELEVATOR_DGAIN = "elevator.dGain";
	public static final String ELEVATOR_FFGAIN = "elevator.ffGain";	
}
