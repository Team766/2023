package com.team766.hal.simulator;

import com.team766.hal.EncoderReader;
import com.team766.simulator.ProgramInterface;

public class Encoder implements EncoderReader{
	
	private final int channel;
	private double distancePerPulse = 1.0;
	
	public Encoder(int a, int b){
		this.channel = a;
	}
	
	public int get() {
		return (int)ProgramInterface.encoderChannels[channel];
	}

	public void reset() {
		set(0);
	}

	public boolean getStopped() {
		// TODO
		return false;
	}

	public boolean getDirection() {
		// TODO
		return false;
	}

	public double getDistance() {
		return get() * distancePerPulse;
	}

	public double getRate() {
		// TODO
		return 0;
	}

	public void setDistancePerPulse(double distancePerPulse) {
		this.distancePerPulse = distancePerPulse;
	}
	
	public void set(int tick){
		ProgramInterface.encoderChannels[channel] = tick;
	}

}
