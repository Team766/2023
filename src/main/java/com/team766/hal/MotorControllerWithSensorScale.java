package com.team766.hal;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;

public class MotorControllerWithSensorScale implements MotorController {
	private MotorController delegate;
	private double scale;

	public MotorControllerWithSensorScale(MotorController delegate, double scale) {
		this.delegate = delegate;
		this.scale = scale;
	}

	@Override
	public double getSensorPosition() {
		return delegate.getSensorPosition() * scale;
	}

	@Override
	public double getSensorVelocity() {
		return delegate.getSensorVelocity() * scale;
	}

	@Override
	public void set(ControlMode mode, double value) {
		switch (mode) {
		case PercentOutput:
			delegate.set(mode, value);
			return;
		case Position:
			delegate.set(mode, value / scale);
			return;
		case Velocity:
			delegate.set(mode, value / scale);
			return;
		case Current:
			delegate.set(mode, value);
			return;
		case Voltage:
			delegate.set(mode, value);
		case Follower:
			delegate.set(mode, value);
			return;
		case MotionProfile:
			// TODO: What is value here? This assumes its a target position.
			delegate.set(mode, value / scale);
			return;
		case MotionMagic:
			// TODO: What is value here? This assumes its a target position.
			delegate.set(mode, value / scale);
			return;
		case MotionProfileArc:
			// TODO: What is value here? This assumes its a target position.
			delegate.set(mode, value / scale);
			return;
		case Disabled:
			delegate.set(mode, value);
			return;
		}
		throw new UnsupportedOperationException("Unimplemented control mode in MotorControllerWithSensorScale");
	}

	@Override
	public void setInverted(boolean isInverted) {
		delegate.setInverted(isInverted);
	}

	@Override
	public boolean getInverted() {
		return delegate.getInverted();
	}

	@Override
	public void stopMotor() {
		delegate.stopMotor();
	}

	@Override
	public void setSensorPosition(double position) {
		delegate.setSensorPosition(position / scale);
	}

	@Override
	public void follow(MotorController leader) {
		delegate.follow(leader);
	}

	@Override
	public void setNeutralMode(NeutralMode neutralMode) {
		delegate.setNeutralMode(neutralMode);
	}

	@Override
	public void setP(double value) {
		delegate.setP(value / scale);
	}

	@Override
	public void setI(double value) {
		delegate.setI(value / scale);
	}

	@Override
	public void setD(double value) {
		delegate.setD(value / scale);
	}

	@Override
	public void setFF(double value) {
		delegate.setFF(value / scale);
	}

	@Override
	public void setSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
		delegate.setSelectedFeedbackSensor(feedbackDevice);
	}

	@Override
	public void setSensorInverted(boolean inverted) {
		delegate.setSensorInverted(inverted);
	}

	@Override
	public void setOutputRange(double minOutput, double maxOutput) {
		delegate.setOutputRange(minOutput, maxOutput);
	}
	
	@Override
	public void setCurrentLimit(double ampsLimit) {
		delegate.setCurrentLimit(ampsLimit);
	}

	@Override
	public void restoreFactoryDefault() {
		delegate.restoreFactoryDefault();
	}

	@Override
	public void setOpenLoopRamp(double secondsFromNeutralToFull) {
		delegate.setOpenLoopRamp(secondsFromNeutralToFull);
	}

	@Override
	public void setClosedLoopRamp(double secondsFromNeutralToFull) {
		delegate.setClosedLoopRamp(secondsFromNeutralToFull);
	}

	@Override
	public double get() {
		return delegate.get();
	}

	@Override
	public void set(double power) {
		delegate.set(power);
	}
}
