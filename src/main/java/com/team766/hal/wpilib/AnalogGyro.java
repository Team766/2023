package com.team766.hal.wpilib;

import com.team766.hal.GyroReader;

public class AnalogGyro extends edu.wpi.first.wpilibj.AnalogGyro implements GyroReader {
	public AnalogGyro(int channel) {
		super(channel);
	}
}
