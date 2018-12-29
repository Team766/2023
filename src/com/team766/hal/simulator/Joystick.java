package com.team766.hal.simulator;

import com.team766.hal.JoystickReader;
import com.team766.simulator.ProgramInterface;

public class Joystick implements JoystickReader{
	
	private ProgramInterface.JoystickCommunication channel;
	private boolean previousTriggerValue;
	
	public Joystick(int channel){
		this.channel = ProgramInterface.joystickChannels[channel];
	}
	
	@Override
	public double getRawAxis(int axis) {
		return channel.axisValues[axis];
	}

	@Override
	public boolean getRawButton(int button) {
		return channel.buttonValues[button];
	}
	
	public void setAxisValue(int axis, double value){
		channel.axisValues[axis] = value;
	}
	
	public void setButton(int button, boolean val){
		channel.buttonValues[button] = val;
	}

	@Override
	public int getPOV() {
		return channel.povValue;
	}
	
	public void setPOV(int value) {
		channel.povValue = value;
	}

	@Override
	public boolean getTrigger() {
		return getRawButton(0);
	}

	@Override
	public boolean getTriggerPressed() {
		boolean currentValue = getTrigger();
		boolean pressed = currentValue && !previousTriggerValue;
		previousTriggerValue = currentValue;
		return pressed;
	}

}
