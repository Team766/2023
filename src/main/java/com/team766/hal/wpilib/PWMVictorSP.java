package com.team766.hal.wpilib;

import com.team766.hal.BasicMotorController;

import edu.wpi.first.wpilibj.motorcontrol.VictorSP;

public class PWMVictorSP extends VictorSP implements BasicMotorController {
	public PWMVictorSP(int channel) {
		super(channel);
	}

	@Override
	public void restoreFactoryDefault() {
		// No-op
	}
}
