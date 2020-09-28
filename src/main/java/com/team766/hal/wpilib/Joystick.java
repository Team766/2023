package com.team766.hal.wpilib;

import com.team766.hal.JoystickReader;

public class Joystick extends edu.wpi.first.wpilibj.Joystick implements JoystickReader {
	public Joystick(int port) {
		super(port);
	}

	@Override
	public double getAxis(int axis) {
		return getRawAxis(axis);
	}

	@Override
	public boolean getButton(int button) {
		return getRawButton(button);
	}

	@Override
	public boolean getButtonPressed(int button) {
		return getRawButtonPressed(button);
	}

	@Override
	public boolean getButtonReleased(int button) {
		return getRawButtonReleased(button);
	}
}
