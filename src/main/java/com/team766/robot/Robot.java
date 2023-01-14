package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
	public static Drive drive;
	public static PhotonVision photonVision;
	public static Gyro gyro;
	public static void robotInit() {

		// Initialize mechanisms here
		
		drive = new Drive();
		photonVision = new PhotonVision();
		gyro = new Gyro();
	}
}
