package com.team766.hal.wpilib;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.team766.hal.CANSpeedController;
import com.team766.hal.SpeedControllerCommandFailedException;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public class CANTalonSpeedController implements CANSpeedController {

	static final int TIMEOUT_MS = 20;

	private WPI_TalonSRX m_device;
	private double m_feedForward = 0.0;

	public CANTalonSpeedController(int deviceNumber) {
		m_device = new WPI_TalonSRX(deviceNumber);
	}

	static void errorCodeToException(ErrorCode err) {
		throw new SpeedControllerCommandFailedException(err.toString());
	}

	@Override
	public void set(ControlMode mode, double value) {
		com.ctre.phoenix.motorcontrol.ControlMode ctre_mode = null;
		boolean useFourTermSet = true;
		switch (mode) {
		case PercentOutput:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;
			useFourTermSet = false;
			break;
		case Position:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.Position;
			break;
		case Velocity:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.Velocity;
			break;
		case Current:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.Current;
			break;
		case Follower:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.Follower;
			useFourTermSet = false;
			break;
		case MotionProfile:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.MotionProfile;
			break;
		case MotionMagic:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.MotionMagic;
			break;
		case MotionProfileArc:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.MotionProfileArc;
			break;
		case Disabled:
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.Disabled;
			useFourTermSet = false;
			break;
		}
		if (ctre_mode == null) {
			Logger.get(Category.HAL).logRaw(
					Severity.ERROR,
					"CAN ControlMode is not translatable: " + mode);
			ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.Disabled;
		}
		if (useFourTermSet) {
			m_device.set(ctre_mode, value, DemandType.ArbitraryFeedForward, m_feedForward);
		} else {
			m_device.set(ctre_mode, value);
		}
	}

	@Override
	public void stopMotor() {
		m_device.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, 0);
	}

	@Override
	public double getSensorPosition() {
		return m_device.getSelectedSensorPosition(0);
	}

	@Override
	public double getSensorVelocity() {
		return m_device.getSelectedSensorVelocity(0);
	}
	
	@Override
	public void setPosition(int position){
		errorCodeToException(m_device.setSelectedSensorPosition(position, 0, 0));
	}

	@Override
	public void follow(CANSpeedController leader) {
		try {
			m_device.follow((IMotorController)leader);
		} catch (ClassCastException ex) {
			throw new IllegalArgumentException("Victor can only follow another CTRE motor controller", ex);
		}
	}

	@Override
	public void configOpenLoopRamp(double secondsFromNeutralToFull) {
		errorCodeToException(m_device.configOpenloopRamp(secondsFromNeutralToFull, TIMEOUT_MS));
	}

	@Override
	public void configClosedLoopRamp(double secondsFromNeutralToFull) {
		errorCodeToException(m_device.configClosedloopRamp(secondsFromNeutralToFull, TIMEOUT_MS));
	}

	@Override
	public void config_kF(int slotIdx, double value) {
		errorCodeToException(m_device.config_kF(slotIdx, value, TIMEOUT_MS));
	}

	@Override
	public void configMotionCruiseVelocity(int sensorUnitsPer100ms) {
		errorCodeToException(m_device.configMotionCruiseVelocity(sensorUnitsPer100ms));
	}

	@Override
	public void configMotionAcceleration(int sensorUnitsPer100msPerSec) {
		errorCodeToException(m_device.configMotionAcceleration(sensorUnitsPer100msPerSec));
	}

	@Override
	public void config_kP(int slotIdx, double value) {
		errorCodeToException(m_device.config_kP(slotIdx, value));
	}

	@Override
	public void config_kI(int slotIdx, double value) {
		errorCodeToException(m_device.config_kI(slotIdx, value));
	}

	@Override
	public void config_kD(int slotIdx, double value) {
		errorCodeToException(m_device.config_kD(slotIdx, value));
	}

	@Override
	public void configSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
		errorCodeToException(m_device.configSelectedFeedbackSensor(feedbackDevice));
	}

	@Override
	public void configNominalOutputForward(double PercentOutput) {
		errorCodeToException(m_device.configNominalOutputForward(PercentOutput));
	}

	@Override
	public void configNominalOutputReverse(double PercentOutput) {
		errorCodeToException(m_device.configNominalOutputReverse(PercentOutput));
	}

	@Override
	public void configPeakOutputForward(double PercentOutput) {
		errorCodeToException(m_device.configPeakOutputForward(PercentOutput));
	}

	@Override
	public void configPeakOutputReverse(double PercentOutput) {
		errorCodeToException(m_device.configPeakOutputReverse(PercentOutput));
	}

	@Override
	public void configFactoryDefault() {
		errorCodeToException(m_device.configFactoryDefault());
	}

	@Override
	public double get() {
		return m_device.get();
	}

	@Override
	public void set(double speed) {
		m_device.set(speed);
	}

	@Override
	public void setInverted(boolean isInverted) {
		m_device.setInverted(isInverted);
	}

	@Override
	public boolean getInverted() {
		return m_device.getInverted();
	}

	@Override
	public void setNeutralMode(NeutralMode neutralMode) {
		m_device.setNeutralMode(neutralMode);
	}

	@Override
	public void setSensorPhase(boolean PhaseSensor) {
		m_device.setSensorPhase(PhaseSensor);
	}
	
}
