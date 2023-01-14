package com.team766.hal.wpilib;

import com.team766.hal.EncoderReader;

public class Encoder extends edu.wpi.first.wpilibj.Encoder implements EncoderReader {
	public Encoder(int channelA, int channelB) {
		super(channelA, channelB);
	}
}
