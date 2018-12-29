package com.team766.controllers;

import com.team766.config.ConfigFileReader;
import com.team766.library.ConstantValueProvider;
import com.team766.library.ValueProvider;

/*
 * Limits the given value to be between between configured min and max values
 */
public class RangeBound {
	private ValueProvider<Double> m_min;
	private ValueProvider<Double> m_max;
	
	public static RangeBound loadFromConfig(String configPrefix) {
		if (!configPrefix.endsWith(".")) {
			configPrefix += ".";
		}
		return new RangeBound(
				ConfigFileReader.getInstance().getDouble(configPrefix + "min"),
				ConfigFileReader.getInstance().getDouble(configPrefix + "max"));
	}
	
	public RangeBound(double min, double max) {
		m_min = new ConstantValueProvider<Double>(min);
		m_max = new ConstantValueProvider<Double>(max);
	}
	
	public RangeBound(ValueProvider<Double> min, ValueProvider<Double> max) {
		m_min = min;
		m_max = max;
	}
	
	public double filter(double input) {
		return Math.min(Math.max(input, m_min.get()), m_max.get());
	}
}
