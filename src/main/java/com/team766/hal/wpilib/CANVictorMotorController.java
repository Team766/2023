package com.team766.hal.wpilib;

import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.team766.hal.MotorController;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;

public class CANVictorMotorController extends BaseCTREMotorController implements MotorController {

	private WPI_VictorSPX m_device;
	private double m_feedForward = 0.0;
	
	public CANVictorMotorController(int deviceNumber) {
		m_device = new WPI_VictorSPX(deviceNumber);
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
			// Sensor velocity is measured in units per 100ms.
			value /= 10.0;
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
		case Voltage:
			m_device.setVoltage(value);
			return;
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
		// Sensor velocity is returned in units per 100ms.
		return m_device.getSelectedSensorVelocity(0) * 10.0;
	}
	
	@Override
	public void setSensorPosition(double position){
		errorCodeToException(ExceptionTarget.THROW, m_device.setSelectedSensorPosition(position, 0, 20));
	}

	@Override
	public void follow(MotorController leader) {
		try {
			m_device.follow((IMotorController)leader);
		} catch (ClassCastException ex) {
			LoggerExceptionUtils.logException(new IllegalArgumentException("Victor can only follow another CTRE motor controller", ex));
		}
	}

	@Override
	public void setOpenLoopRamp(double secondsFromNeutralToFull) {
		errorCodeToException(ExceptionTarget.LOG, m_device.configOpenloopRamp(secondsFromNeutralToFull, TIMEOUT_MS));
	}

	@Override
	public void setClosedLoopRamp(double secondsFromNeutralToFull) {
		errorCodeToException(ExceptionTarget.LOG, m_device.configClosedloopRamp(secondsFromNeutralToFull, TIMEOUT_MS));
	}

	@Override
	public void setFF(double value) {
		errorCodeToException(ExceptionTarget.LOG, m_device.config_kF(0, value, TIMEOUT_MS));
	}

	@Override
	public double get() {
		return m_device.get();
	}

	@Override
	public void set(double power) {
		m_device.set(power);
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
	public void setP(double value) {
		errorCodeToException(ExceptionTarget.LOG, m_device.config_kP(0, value, TIMEOUT_MS));
	}

	@Override
	public void setI(double value) {
		errorCodeToException(ExceptionTarget.LOG, m_device.config_kI(0, value, TIMEOUT_MS));
	}

	@Override
	public void setD(double value) {
		errorCodeToException(ExceptionTarget.LOG, m_device.config_kD(0, value, TIMEOUT_MS));
	}

	@Override
	public void setSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
		errorCodeToException(ExceptionTarget.LOG, m_device.configSelectedFeedbackSensor(feedbackDevice));
	}

	@Override
	public void setSensorInverted(boolean inverted) {
		m_device.setSensorPhase(inverted);
	}

	@Override
	public void setOutputRange(double minOutput, double maxOutput) {
		errorCodeToException(ExceptionTarget.LOG, m_device.configPeakOutputReverse(minOutput));
		errorCodeToException(ExceptionTarget.LOG, m_device.configPeakOutputForward(maxOutput));
	}
	
	@Override
	public void setCurrentLimit(double ampsLimit) {
		LoggerExceptionUtils.logException(
			new UnsupportedOperationException("VictorSPX does not support current limiting"));
	}

	@Override
	public void restoreFactoryDefault() {
		errorCodeToException(ExceptionTarget.LOG, m_device.configFactoryDefault());
	}
}