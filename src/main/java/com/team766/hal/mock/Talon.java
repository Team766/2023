package com.team766.hal.mock;

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
	private CANSpeedController leader;
	
	public Talon(int index) {
		output = 0;
		leader = null;
	}
	
	@Override
	public double get() {
		if (leader != null) {
			return leader.get();
		}
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
		leader = null;
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
		this.leader = leader;
	}

	@Override
	public void setNeutralMode(NeutralMode neutralMode) {
		// throw new UnsupportedOperationException();
	}

	// TODO: add actual mock stuff for these functions
	@Override
	public void config_kP(int slotIdx, double value) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void config_kI(int slotIdx, double value) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void config_kD(int slotIdx, double value) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void configSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void configNominalOutputForward(double PercentOutput) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void configNominalOutputReverse(double PercentOutput) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void configPeakOutputForward(double PercentOutput) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void configPeakOutputReverse(double PercentOutput) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void setSensorPhase(boolean PhaseSensor) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void configFactoryDefault() {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void configOpenLoopRamp(double secondsFromNeutralToFull) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void configClosedLoopRamp(double secondsFromNeutralToFull) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void config_kF(int slotIdx, double value) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void configMotionCruiseVelocity(int sensorUnitsPer100ms) {
		motionCruiseVelocity = sensorUnitsPer100ms;
	}

	@Override
	public void configMotionAcceleration(int sensorUnitsPer100msPerSec) {
		motionCruiseAcceleration = sensorUnitsPer100msPerSec;
	}
}
