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
	public void setP(double value) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void setI(double value) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void setD(double value) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void setSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void setSensorInverted(boolean inverted) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void setOutputRange(double minOutput, double maxOutput) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void setCurrentLimit(double ampsLimit) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void restoreFactoryDefault() {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void setOpenLoopRamp(double secondsFromNeutralToFull) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void setClosedLoopRamp(double secondsFromNeutralToFull) {
		// throw new UnsupportedOperationException();
	}

	@Override
	public void setFF(double value) {
		// throw new UnsupportedOperationException();
	}

}
