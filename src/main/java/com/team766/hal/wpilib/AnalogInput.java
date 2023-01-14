package com.team766.hal.wpilib;

import com.team766.hal.AnalogInputReader;

public class AnalogInput extends edu.wpi.first.wpilibj.AnalogInput implements AnalogInputReader {
	public AnalogInput(int channel) {
		super(channel);
	}
}
