package com.team766.hal.wpilib;

import com.team766.hal.BasicSpeedController;

import edu.wpi.first.wpilibj.motorcontrol.VictorSP;

public class PWMVictorSP extends VictorSP implements BasicSpeedController {
	public PWMVictorSP(int channel) {
		super(channel);
	}

	@Override
	public void restoreFactoryDefault() {
		// No-op
	}
}
