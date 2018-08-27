package com.team766.controllers;

import com.team766.config.ConfigFileReader;

public class RangeOfMotionBound {
	private double m_minPosition;
	private double m_maxPosition;
	private boolean m_sensorInverted;
	
	public static RangeOfMotionBound loadFromConfig(String configPrefix) {
		if (!configPrefix.endsWith(".")) {
			configPrefix += ".";
		}
		return new RangeOfMotionBound(
				ConfigFileReader.getInstance().getDouble(configPrefix + "minPosition"),
				ConfigFileReader.getInstance().getDouble(configPrefix + "maxPosition"),
				ConfigFileReader.getInstance().getBoolean(configPrefix + "sensorInverted"));
	}
	
	public RangeOfMotionBound(double minPosition, double maxPosition, boolean sensorInverted) {
		m_minPosition = minPosition;
		m_maxPosition = maxPosition;
		m_sensorInverted = sensorInverted;
	}
	
	public double filter(double inputCommand, double sensorPosition) {
		double normalizedCommand = inputCommand;
		if (m_sensorInverted) {
			normalizedCommand *= -1;
		}
		if (sensorPosition < m_minPosition && normalizedCommand < 0) {
			inputCommand = 0;
		}
		if (sensorPosition >= m_maxPosition && normalizedCommand > 0) {
			inputCommand = 0;
		}
		return inputCommand;
	}
}
