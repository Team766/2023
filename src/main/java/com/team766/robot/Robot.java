package com.team766.robot;

import com.team766.robot.mechanisms.*;
import edu.wpi.first.wpilibj.I2C;

public class Robot {
	// Declare mechanisms here
	public static MultiplexedColorSensorV3 topColorSensor;
	public static MultiplexedColorSensorV3 bottomColorSensor;

	public static void robotInit() {
		// Initialize mechanisms here
		topColorSensor = new MultiplexedColorSensorV3(I2C.Port.kOnboard, 1);
		bottomColorSensor = new MultiplexedColorSensorV3(I2C.Port.kOnboard, 7);
	}
}
