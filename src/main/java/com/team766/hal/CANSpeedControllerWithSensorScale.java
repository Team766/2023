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
	public void config_kP(int slotIdx, double value) {
        delegate.config_kP(slotIdx, value / scale);
    }

    @Override
	public void config_kI(int slotIdx, double value) {
        delegate.config_kI(slotIdx, value / scale);
    }

    @Override
	public void config_kD(int slotIdx, double value) {
        delegate.config_kD(slotIdx, value / scale);
    }

    @Override
	public void config_kF(int slotIdx, double value) {
        delegate.config_kF(slotIdx, value / scale);
    }

    @Override
	public void configSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
        delegate.configSelectedFeedbackSensor(feedbackDevice);
    }

    @Override
	public void configNominalOutputForward(double PercentOutput) {
        delegate.configNominalOutputForward(PercentOutput);
    }

    @Override
	public void configNominalOutputReverse(double PercentOutput) {
        delegate.configNominalOutputReverse(PercentOutput);
    }

    @Override
	public void configPeakOutputForward(double PercentOutput) {
        delegate.configPeakOutputForward(PercentOutput);
    }

    @Override
	public void configPeakOutputReverse(double PercentOutput) {
        delegate.configPeakOutputReverse(PercentOutput);
    }

    @Override
	public void configMotionCruiseVelocity(int sensorUnitsPer100ms) {
        delegate.configMotionCruiseVelocity((int)Math.round(sensorUnitsPer100ms / scale));
    }

    @Override
	public void configMotionAcceleration(int sensorunitsPer100msPerSec) {
        delegate.configMotionAcceleration((int)Math.round(sensorunitsPer100msPerSec / scale));
    }

    @Override
	public void setSensorPhase(boolean PhaseSensor) {
        delegate.setSensorPhase(PhaseSensor);
    }

    @Override
	public void configFactoryDefault() {
        delegate.configFactoryDefault();
    }

    @Override
	public void configOpenLoopRamp(double secondsFromNeutralToFull) {
        delegate.configOpenLoopRamp(secondsFromNeutralToFull);
    }

    @Override
	public void configClosedLoopRamp(double secondsFromNeutralToFull) {
        delegate.configClosedLoopRamp(secondsFromNeutralToFull);
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
