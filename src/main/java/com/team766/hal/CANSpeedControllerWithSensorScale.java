package com.team766.hal;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;

public class CANSpeedControllerWithSensorScale implements CANSpeedController {
    private CANSpeedController delegate;
    private double scale;

    public CANSpeedControllerWithSensorScale(CANSpeedController delegate, double scale) {
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
            set(mode, value);
            return;
        case Position:
            set(mode, value / scale);
            return;
        case Velocity:
            set(mode, value / scale);
            return;
        case Current:
            set(mode, value);
            return;
        case Follower:
            set(mode, value);
            return;
        case MotionProfile:
            // TODO: What is value here? This assumes its a target position.
            set(mode, value / scale);
            return;
        case MotionMagic:
            // TODO: What is value here? This assumes its a target position.
            set(mode, value / scale);
            return;
        case MotionProfileArc:
            // TODO: What is value here? This assumes its a target position.
            set(mode, value / scale);
            return;
        case Disabled:
            set(mode, value);
            return;
        }
        throw new UnsupportedOperationException("Unimplemented control mode in CANSpeedControllerWithSensorScale");
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
	public void setPosition(int position) {
        delegate.setPosition((int)Math.round(position / scale));
    }

    @Override
	public void follow(CANSpeedController leader) {
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
    public void set(double speed) {
        delegate.set(speed);
    }
}
