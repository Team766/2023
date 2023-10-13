package com.team766.robot.constants;

public final class SwerveDriveConstants {

	// defines where the wheels are in relation to the center of the robot
	// allows swerve drive code to calculate how to turn
	public static final double FL_X = -1;
	public static final double FL_Y = 1;
	public static final double FR_X = 1;
	public static final double FR_Y = 1;
	public static final double BL_X = -1;
	public static final double BL_Y = -1;
	public static final double BR_X = 1;
	public static final double BR_Y = -1;

	public static final String SWERVE_CANBUS = "Swervavore";
	
	public static final double DRIVE_MOTOR_CURRENT_LIMIT = 35;
	public static final double STEER_MOTOR_CURRENT_LIMIT = 30;

}
