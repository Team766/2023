package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
	public static TestSolenoid testSolenoid;
	

	public static void robotInit() {
		// Initialize mechanisms here
		testSolenoid = new TestSolenoid();
	}
}
