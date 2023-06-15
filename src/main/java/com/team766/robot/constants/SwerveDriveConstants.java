package com.team766.robot.constants;

public final class SwerveDriveConstants {

	public static double fl_x = -1;
	public static double fl_y = 1;
	public static double fr_x = 1;
	public static double fr_y = 1;
	public static double bl_x = -1;
	public static double bl_y = -1;
	public static double br_x = 1;
	public static double br_y = -1;

	// public static double FirstFrontRightAngle = 135;
	// public static double FirstFrontLeftAngle = 45;
	// public static double FirstBackRightAngle = -135;
	// public static double FirstBackLeftAngle = -45;

	// public static double SecondFrontRightAngle = -45;
	// public static double SecondFrontLeftAngle = -135;
	// public static double SecondBackRightAngle = 45;
	// public static double SecondBackLeftAngle = 135;

	public static double ratio = 2048.0 / 360.0 * (150.0 / 7.0);

	public static double P = 0.2;
	public static double I = 0.0;
	public static double D = 0.1;
	public static double FF = 0.0;

	public static double CrossFrontRightAngle = -45;
	public static double CrossFrontLeftAngle = 45;
	public static double CrossBackRightAngle = 135;
	public static double CrossBackLeftAngle = -135;

	
}
