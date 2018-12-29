package com.team766.controllers;

import com.team766.config.ConfigFileReader;
import com.team766.library.ConstantValueProvider;
import com.team766.library.ValueProvider;

/*
 * Coerces the given motor command to not allow the mechanism to be driven
 * outside of the range of sensor positions between minPosition and maxPosition
 */
public class RangeOfMotionMotorCommandBound {
	private ValueProvider<Double> m_minPosition;
	private ValueProvider<Double> m_maxPosition;
	private ValueProvider<Boolean> m_sensorInverted;
	
	public static RangeOfMotionMotorCommandBound loadFromConfig(String configPrefix) {
		if (!configPrefix.endsWith(".")) {
			configPrefix += ".";
		}
		return new RangeOfMotionMotorCommandBound(
				ConfigFileReader.getInstance().getDouble(configPrefix + "minPosition"),
				ConfigFileReader.getInstance().getDouble(configPrefix + "maxPosition"),
				ConfigFileReader.getInstance().getBoolean(configPrefix + "sensorInverted"));
	}
	
	public RangeOfMotionMotorCommandBound(double minPosition, double maxPosition, boolean sensorInverted) {
		m_minPosition = new ConstantValueProvider<Double>(minPosition);
		m_maxPosition = new ConstantValueProvider<Double>(maxPosition);
		m_sensorInverted = new ConstantValueProvider<Boolean>(sensorInverted);
	}
	
	public RangeOfMotionMotorCommandBound(
			ValueProvider<Double> minPosition,
			ValueProvider<Double> maxPosition,
			ValueProvider<Boolean> sensorInverted) {
		m_minPosition = minPosition;
		m_maxPosition = maxPosition;
		m_sensorInverted = sensorInverted;
	}
	
	public double filter(double inputCommand, double sensorPosition) {
		double normalizedCommand = inputCommand;
		if (m_sensorInverted.get()) {
			normalizedCommand *= -1;
		}
		if (sensorPosition < m_minPosition.get() && normalizedCommand < 0) {
			inputCommand = 0;
		}
		if (sensorPosition >= m_maxPosition.get() && normalizedCommand > 0) {
			inputCommand = 0;
		}
		return inputCommand;
	}
}
