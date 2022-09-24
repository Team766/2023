package com.team766.hal.mock;

import com.team766.hal.EncoderReader;

public class MockEncoder implements EncoderReader{
	
	private double distance = 0;
	private double rate = 0;
	private double distancePerPulse = 1;
	
	public MockEncoder(int a, int b){
	}
	
	@Override
	public int get() {
		return (int)Math.round(distance / distancePerPulse);
	}

	@Override
	public void reset() {
		distance = 0;
	}

	@Override
	public boolean getStopped() {
		return this.rate == 0;
	}

	@Override
	public boolean getDirection() {
		return this.rate > 0;
	}

	@Override
	public double getDistance() {
		return this.distance;
	}

	@Override
	public double getRate() {
		return this.rate;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public void setRate(double rate){
		this.rate = rate;
	}

	@Override
	public void setDistancePerPulse(double distancePerPulse) {
		this.distancePerPulse = distancePerPulse;
	}

}
