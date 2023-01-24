package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
	public static ColorSensorMech cSensor;
	public static ColorMatchMech cMatcher;

	public static void robotInit() {
		// Initialize mechanisms here
		cSensor = new ColorSensorMech();
		cMatcher = new ColorMatchMech();
	}
}
