package com.team766.robot;

import com.team766.hal.GyroReader;
import com.team766.hal.RobotProvider;
import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
    public static Drive drive;	
    //public static GyroReader imu;

	public static void robotInit() {
		// Initialize mechanisms here
        drive = new Drive();
		//imu = RobotProvider.instance.getGyro("imu");		
	}
}
