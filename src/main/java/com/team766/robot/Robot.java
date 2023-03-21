package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
	public static Intake intake; 
	public static Storage storage;
	public static Drive drive;
	public static Grabber grabber;
	public static Arms arms;
	public static Gyro gyro;

	public static void robotInit() {
		// Initialize mechanisms here
		intake = new Intake();
		storage = new Storage();
		drive = new Drive();
		grabber = new Grabber();
		arms = new Arms();
		gyro = new Gyro();
	}
}
