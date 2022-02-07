package com.team766.hal.wpilib;

import com.team766.hal.GyroReader;

public class AnalogGyro extends edu.wpi.first.wpilibj.AnalogGyro implements GyroReader {
	public AnalogGyro(int channel) {
		super(channel);
	}

	public double getPitch() {
		return 0.0;
	}

	public double getRoll() {
		return 0.0;
	}
}
