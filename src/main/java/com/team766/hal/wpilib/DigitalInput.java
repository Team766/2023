package com.team766.hal.wpilib;

import com.team766.hal.DigitalInputReader;

public class DigitalInput extends edu.wpi.first.wpilibj.DigitalInput implements DigitalInputReader {
	public DigitalInput(int channel) {
		super(channel);
	}
}
