package com.team766.hal.mock;

import com.team766.hal.GyroReader;

public class Gyro implements GyroReader{

	private double angle = 0;
	private double rate = 0;
	
	public void calibrate() {
		reset();
	}

	public void reset() {
		angle = 0;
	}

	public double getAngle() {
		return angle;
	}

	public double getRate() {
		return rate;
	}
	
	public void setAngle(double ang){
		angle = ang;
	}
	
	public void setRate(double rat){
		rate = rat;
	}

}
