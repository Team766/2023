package com.team766.hal.mock;

import com.team766.hal.JoystickReader;

public class Joystick implements JoystickReader{
	
	private double[] axisValues;
	private boolean[] buttonValues;
	private boolean previousTriggerValue;
	private int povValue;
	
	public Joystick(int index){
		axisValues = new double[4];	//Fix size to reflect actual joysticks
		buttonValues = new boolean[20];	//Fix size to reflect actual joysticks
		previousTriggerValue = false;
	}
	
	@Override
	public double getRawAxis(int axis) {
		return axisValues[axis];
	}

	@Override
	public boolean getRawButton(int button) {
		return buttonValues[button];
	}
	
	public void setAxisValue(int axis, double value){
		axisValues[axis] = value;
	}
	
	public void setButton(int button, boolean val){
		buttonValues[button] = val;
	}

	@Override
	public int getPOV() {
		return povValue;
	}
	
	public void setPOV(int value) {
		povValue = value;
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
