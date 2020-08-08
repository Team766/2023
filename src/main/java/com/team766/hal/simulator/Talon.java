package com.team766.hal.simulator;

import com.team766.hal.CANSpeedController;
import com.team766.simulator.ProgramInterface;

public class Talon implements CANSpeedController{

	private ProgramInterface.CANSpeedControllerCommunication channel;
	
	public Talon(int address){
		this.channel = ProgramInterface.canSpeedControllerChannels[address];
	}
	
	@Override
	public double get() {
		return channel.command.output;
	}
	
	@Override
	public void set(double speed) {
		channel.command.output = speed;
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
		channel.command.output = 0;
	}

	public void setSensorPosition(double position) {
		channel.status.sensorPosition = position;
	}
	
	@Override
	public double getSensorPosition() {
		return channel.status.sensorPosition;
	}
	
	public void setSensorVelocity(double velocity) {
		channel.status.sensorVelocity = velocity;
	}

	@Override
	public double getSensorVelocity() {
		return channel.status.sensorVelocity;
	}

	@Override
	public void set(ControlMode mode, double value) {
		channel.command.output = value;
		channel.command.controlMode = 
				ProgramInterface.CANSpeedControllerCommand.ControlMode.valueOf(mode.name());
	}
	
	public ControlMode getControlMode() {
		return ControlMode.valueOf(channel.command.controlMode.name());
	}

	@Override
	public void setPosition(int position) {
		channel.status.sensorPosition = position;
	}
	
}
