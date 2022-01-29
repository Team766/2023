package com.team766.hal.wpilib;

import com.team766.hal.SpeedController;

import edu.wpi.first.wpilibj.motorcontrol.VictorSP;

public class Victor extends VictorSP implements SpeedController {
	public Victor(int channel) {
		super(channel);
	}
}
