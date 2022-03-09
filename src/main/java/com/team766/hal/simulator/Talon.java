package com.team766.hal.simulator;

import static com.team766.math.Math.clamp;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team766.hal.CANSpeedController;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.simulator.ProgramInterface;

public class Talon implements CANSpeedController {

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
		set(ControlMode.PercentOutput, speed);
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
		if (mode == ControlMode.PercentOutput) {
			value = clamp(value, -1.0, 1.0);
		}
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

	@Override
	public void follow(CANSpeedController leader) {
		LoggerExceptionUtils.logException(new UnsupportedOperationException("follow() is currently unsupported in the simulator"));
	}
	
	@Override
	public void setNeutralMode(NeutralMode neutralMode) {
		LoggerExceptionUtils.logException(new UnsupportedOperationException("setNeutralMode() is currently unsupported in the simulator"));
	}

	@Override
	public void setP(double value) {
		LoggerExceptionUtils.logException(new UnsupportedOperationException("setP() is currently unsupported in the simulator"));
	}

	@Override
	public void setI(double value) {
		LoggerExceptionUtils.logException(new UnsupportedOperationException("setI() is currently unsupported in the simulator"));
	}

	@Override
	public void setD(double value) {
		LoggerExceptionUtils.logException(new UnsupportedOperationException("setD() is currently unsupported in the simulator"));
	}

	@Override
	public void setFF(double value) {
		LoggerExceptionUtils.logException(new UnsupportedOperationException("setFF() is currently unsupported in the simulator"));
	}

	@Override
	public void setSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
		LoggerExceptionUtils.logException(new UnsupportedOperationException("setSelectedFeedbsckSensor() is currently unsupported in the simulator"));
	}

	@Override
	public void setSensorInverted(boolean inverted) {
		LoggerExceptionUtils.logException(new UnsupportedOperationException("setSensorInverted() is currently unsupported in the simulator"));
	}

	@Override
	public void setOutputRange(double minOutput, double maxOutput) {
		LoggerExceptionUtils.logException(new UnsupportedOperationException("setOutputRange() is currently unsupported in the simulator"));
	}

	@Override
	public void restoreFactoryDefault() {
		LoggerExceptionUtils.logException(new UnsupportedOperationException("restoreFactoryDefault() is currently unsupported in the simulator"));
	}

	@Override
	public void setOpenLoopRamp(double secondsFromNeutralToFull) {
		LoggerExceptionUtils.logException(new UnsupportedOperationException("setOpenLoopRamp() is currently unsupported in the simulator"));
	}

	@Override
	public void setClosedLoopRamp(double secondsFromNeutralToFull) {
		LoggerExceptionUtils.logException(new UnsupportedOperationException("setClosedLoopRamp() is currently unsupported in the simulator"));
	}
}
