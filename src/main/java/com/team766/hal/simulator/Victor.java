package com.team766.hal.simulator;

import com.team766.hal.SpeedController;
import com.team766.simulator.ProgramInterface;
import static com.team766.math.Math.clamp;

public class Victor implements SpeedController{

	private int channel;
	
	public Victor(int channel){
		this.channel = channel;
	}

	@Override
	public double get() {
		return ProgramInterface.pwmChannels[channel];
	}

	@Override
	public void set(double speed) {
		ProgramInterface.pwmChannels[channel] = clamp(speed, -1.0, 1.0);
	}

	@Override
	public void setInverted(boolean isInverted) {
	}

	@Override
	public boolean getInverted() {
		return false;
	}

	@Override
	public void stopMotor() {
		ProgramInterface.pwmChannels[channel] = 0;
	}
	
}
