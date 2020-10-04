package com.team766.hal.simulator;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team766.hal.CANSpeedController;
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
		// TODO: clamp to -1..1 when in PercentOutput mode
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

	@Override
	public void follow(CANSpeedController leader) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setNeutralMode(NeutralMode neutralMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ErrorCode config_kP(int slotIdx, double value, int timeoutMs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ErrorCode config_kI(int slotIdx, double value, int timeoutMs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ErrorCode config_kD(int slotIdx, double value, int timeoutMs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ErrorCode configNominalOutputForward(double PercentOutput) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ErrorCode configNominalOutputReverse(double PercentOutput) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ErrorCode configPeakOutputForward(double PercentOutput) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ErrorCode configPeakOutputReverse(double PercentOutput) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSensorPhase(boolean PhaseSensor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ErrorCode configFactoryDefault() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configOpenLoopRamp(double secondsFromNeutralToFull, int timeoutMs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configClosedLoopRamp(double secondsFromNeutralToFull, int timeoutMs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ErrorCode config_kF(int slotIdx, double value, int timeoutMs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ErrorCode configMotionCruiseVelocity(int sensorUnitsPer100ms) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ErrorCode configMotionAcceleration(int sensorUnitsPer100msPerSec) {
		throw new UnsupportedOperationException();
	}
}
