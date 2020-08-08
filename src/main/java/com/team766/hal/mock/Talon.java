package com.team766.hal.mock;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team766.hal.CANSpeedController;

public class Talon implements CANSpeedController {

	private double output;
	private double sensorPosition;
	private double sensorVelocity;
	private ControlMode controlMode;

	private double motionCruiseVelocity;
	private double motionCruiseAcceleration;
	
	public Talon(int index) {
		output = 0;
	}
	
	@Override
	public double get() {
		return output;
	}
	
	@Override
	public void set(double speed) {
		output = speed;
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
		output = 0;
	}

	public void setSensorPosition(double position) {
		sensorPosition = position;
	}
	
	@Override
	public double getSensorPosition() {
		return sensorPosition;
	}
	
	public void setSensorVelocity(double velocity) {
		sensorVelocity = velocity;
	}

	@Override
	public double getSensorVelocity() {
		return sensorVelocity;
	}

	@Override
	public void set(ControlMode mode, double value) {
		output = value;
		controlMode = mode;
	}
	
	public ControlMode getControlMode() {
		return controlMode;
	}

	@Override
	public void setPosition(int position) {
		sensorPosition = position;
	}

	@Override
	public void follow(CANSpeedController leader) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setNeutralMode(NeutralMode neutralMode) {
		throw new UnsupportedOperationException();
	}

	// TODO: add actual mock stuff for these functions
	@Override
	public ErrorCode config_kP(int slotIdx, double value, int timeoutMs) {
		// throw new UnsupportedOperationException();
		return ErrorCode.OK;
	}

	@Override
	public ErrorCode config_kI(int slotIdx, double value, int timeoutMs) {
		// throw new UnsupportedOperationException();
		return ErrorCode.OK;
	}

	@Override
	public ErrorCode config_kD(int slotIdx, double value, int timeoutMs) {
		// throw new UnsupportedOperationException();
		return ErrorCode.OK;
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
		// throw new UnsupportedOperationException();
		return ErrorCode.OK;
	}

	@Override
	public ErrorCode configMotionCruiseVelocity(int sensorUnitsPer100ms) {
		motionCruiseVelocity = sensorUnitsPer100ms;
		return ErrorCode.OK;
	}

	@Override
	public ErrorCode configMotionAcceleration(int sensorUnitsPer100msPerSec) {
		motionCruiseAcceleration = sensorUnitsPer100msPerSec;
		return ErrorCode.OK;
	}
}
