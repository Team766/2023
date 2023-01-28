package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
	public static Pulley pulley;

	

	public static void robotInit() {
		// Initialize mechanisms here
		pulley = new Pulley();
		
	}
}
