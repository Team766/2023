package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
	public static Intake intake;
	public static Wrist wrist;
	public static Elevator elevator;

	public static Drive drive;
	public static Gyro gyro;

	public static void robotInit() {
		// Initialize mechanisms here
		intake = new Intake();
		wrist = new Wrist();
		elevator = new Elevator();
		drive = new Drive();
		gyro = new Gyro();
	}
}
