package com.team766.hal.mock;

import com.team766.hal.JoystickReader;

public class MockJoystick implements JoystickReader {
	
	private double[] axisValues;
	private boolean[] buttonValues;
	private boolean[] prevButtonValues;
	private int povValue;
	
	public MockJoystick(){
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
		// Button indexes begin at 1 in WPILib, so match that here
		if (button <= 0) {
			return false;
		}
		return buttonValues[button - 1];
	}
	
	public void setAxisValue(int axis, double value){
		axisValues[axis] = value;
	}
	
	public void setButton(int button, boolean val){
		// Button indexes begin at 1 in WPILib, so match that here
		prevButtonValues[button - 1] = buttonValues[button - 1];
		buttonValues[button - 1] = val;
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
		// Button indexes begin at 1 in WPILib, so match that here
		if (button <= 0) {
			return false;
		}
		return buttonValues[button - 1] && !prevButtonValues[button - 1];
	}

	@Override
	public boolean getButtonReleased(int button) {
		// Button indexes begin at 1 in WPILib, so match that here
		if (button <= 0) {
			return false;
		}
		return !buttonValues[button - 1] && prevButtonValues[button - 1];
	}

}
