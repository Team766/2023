package com.team766.robot;

import com.team766.robot.mechanisms.*;
import edu.wpi.first.wpilibj.I2C;

public class Robot {
	// Declare mechanisms here
	public static ColorSensorMech cSensor;
	public static ColorMatchMech cMatcherA;
	public static ColorMatchMech cMatcherB;

	public static void robotInit() {
		// Initialize mechanisms here
		cSensor = new ColorSensorMech();
		cMatcherA = new ColorMatchMech();
		cMatcherB = new ColorMatchMech();
	}
}
