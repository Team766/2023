package com.team766.hal.mock;

import com.team766.hal.GyroReader;

public class MockGyro implements GyroReader{

	private double angle = 0;
	private double rate = 0;
	private double pitch = 0;
	private double roll = 0;
	
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

	public double getPitch() {
		return pitch;
	}

	public double getRoll() {
		return roll;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}
	
	public void setPitch(double pitch) {
		this.pitch = pitch;
	}
	
	public void setRoll(double roll) {
		this.roll = roll;
	}

}
