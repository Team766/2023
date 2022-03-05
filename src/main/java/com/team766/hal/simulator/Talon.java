package com.team766.hal.simulator;

import static com.team766.math.Math.clamp;

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
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setNeutralMode(NeutralMode neutralMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void config_kP(int slotIdx, double value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void config_kI(int slotIdx, double value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void config_kD(int slotIdx, double value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configNominalOutputForward(double PercentOutput) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configNominalOutputReverse(double PercentOutput) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configPeakOutputForward(double PercentOutput) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configPeakOutputReverse(double PercentOutput) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSensorPhase(boolean PhaseSensor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configFactoryDefault() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configOpenLoopRamp(double secondsFromNeutralToFull) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configClosedLoopRamp(double secondsFromNeutralToFull) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void config_kF(int slotIdx, double value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configMotionCruiseVelocity(int sensorUnitsPer100ms) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configMotionAcceleration(int sensorUnitsPer100msPerSec) {
		throw new UnsupportedOperationException();
	}
}
