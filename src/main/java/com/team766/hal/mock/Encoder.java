package com.team766.hal.mock;

import com.team766.hal.EncoderReader;

public class Encoder implements EncoderReader{
	
	private int ticks = 0;
	
	public Encoder(int a, int b){
	}
	
	public int get() {
		return ticks;
	}

	public void reset() {
		ticks = 0;
	}

	public boolean getStopped() {
		return false;
	}

	public boolean getDirection() {
		return false;
	}

	public double getDistance() {
		return 0;
	}

	public double getRate() {
		return 0;
	}

	public void setDistancePerPulse(double distancePerPulse) {
	}
	
	public void set(int tick){
		ticks = tick;
	}

}
