package com.team766.hal.mock;

import com.team766.hal.JoystickReader;

public class Joystick implements JoystickReader {
	
	private double[] axisValues;
	private boolean[] buttonValues;
	private boolean[] prevButtonValues;
	private int povValue;
	
	public Joystick(){
		axisValues = new double[4];
		buttonValues = new boolean[20];
		prevButtonValues = new boolean[20];
	}
	
	@Override
	public double getAxis(int axis) {
		return axisValues[axis];
	}

	@Override
	public boolean getButton(int button) {
		return buttonValues[button];
	}
	
	public void setAxisValue(int axis, double value){
		axisValues[axis] = value;
	}
	
	public void setButton(int button, boolean val){
		prevButtonValues[button] = buttonValues[button];
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
	public boolean getButtonPressed(int button) {
		return buttonValues[button] && !prevButtonValues[button];
	}

	@Override
	public boolean getButtonReleased(int button) {
		return !buttonValues[button] && prevButtonValues[button];
	}

}
