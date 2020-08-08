package com.team766.hal.wpilib;

import com.team766.hal.JoystickReader;

public class Joystick extends edu.wpi.first.wpilibj.Joystick implements JoystickReader {
	public Joystick(int port) {
		super(port);
	}
}
