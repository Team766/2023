package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
	public static Drive drive;
	public static PhotonVision photonVision;
	public static Gyro gyro;
	public static CANdleMech candle;
	public static Intake intake;
	public static Storage storage;
	public static Arms arms;
	public static Grabber grabber;

	public static void robotInit() {

		// Initialize mechanisms here
		drive = new Drive();
		gyro = new Gyro();
		candle = new CANdleMech();
		intake = new Intake();
		storage = new Storage();
		arms = new Arms();
		grabber = new Grabber();

	}
}
