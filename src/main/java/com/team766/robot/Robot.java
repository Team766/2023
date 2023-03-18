package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
	public static Intake intake; 
	public static Storage storage;

	public static void robotInit() {
		// Initialize mechanisms here
		intake = new Intake();
		storage = new Storage();
	}
}
