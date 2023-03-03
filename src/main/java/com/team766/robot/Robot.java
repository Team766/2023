package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
	public static Arms arms;
	public static ArmsTest armsTest;

	

	public static void robotInit() {
		// Initialize mechanisms here
		arms = new Arms(RobotProvider.instance.getMotor("arms.firstJoint"), RobotProvider.instance.getMotor("arms.secondJoint"));
		armsTest = new ArmsTest();
	}
}
