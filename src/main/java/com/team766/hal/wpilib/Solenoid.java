package com.team766.hal.wpilib;

import com.team766.hal.SolenoidController;

import edu.wpi.first.wpilibj.PneumaticsModuleType;

public class Solenoid extends edu.wpi.first.wpilibj.Solenoid implements SolenoidController {
	public Solenoid(int channel) {
		super(PneumaticsModuleType.CTREPCM, channel);
	}
}
