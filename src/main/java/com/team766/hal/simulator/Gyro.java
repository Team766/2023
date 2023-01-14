package com.team766.hal.simulator;

import com.team766.hal.GyroReader;
import com.team766.simulator.ProgramInterface;

public class Gyro implements GyroReader{

	public void calibrate() {
		reset();
	}

	public void reset() {
		ProgramInterface.gyro.angle = 0;
	}

	public double getAngle() {
		return ProgramInterface.gyro.angle;
	}

	public double getRate() {
		return ProgramInterface.gyro.rate;
	}

	public double getPitch() {
		return ProgramInterface.gyro.pitch;
	}

	public double getRoll() {
		return ProgramInterface.gyro.roll;
	}

}
