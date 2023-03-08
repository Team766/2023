package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {

	// Declare mechanisms here
	public static Arms arms;
	

	public static void robotInit() {
		// Initialize mechanisms here
		arms = new Arms();
	}
}
