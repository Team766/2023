package com.team766.config;

import java.util.Arrays;

class DoubleConfigValue extends AbstractConfigValue<Double> {
	protected DoubleConfigValue(String key) {
		super(key);
	}

	@Override
	public Double parseValue(String configString) {
		return Double.valueOf(configString);
	}
}

class IntegerConfigValue extends AbstractConfigValue<Integer> {
	protected IntegerConfigValue(String key) {
		super(key);
	}

	@Override
	public Integer parseValue(String configString) {
		return Integer.valueOf(configString);
	}
}

class DoubleConfigMultiValue extends AbstractConfigMultiValue<Double> {
	protected DoubleConfigMultiValue(String key) {
		super(key);
	}

	@Override
	public Double[] parseValue(String configString) {
		return (Double[]) Arrays.stream(splitConfigString(configString)).map(Double::valueOf).toArray((size) -> new Double[size]);
	}
}

class IntegerConfigMultiValue extends AbstractConfigMultiValue<Integer> {
	protected IntegerConfigMultiValue(String key) {
		super(key);
	}

	@Override
	public Integer[] parseValue(String configString) {
		return (Integer[]) Arrays.stream(splitConfigString(configString)).map(Integer::valueOf).toArray((size) -> new Integer[size]);
	}
}

class BooleanConfigValue extends AbstractConfigValue<Boolean> {
	protected BooleanConfigValue(String key) {
		super(key);
	}

	@Override
	public Boolean parseValue(String configString) {
		return Boolean.valueOf(configString);
	}
}

class StringConfigValue extends AbstractConfigValue<String> {
	protected StringConfigValue(String key) {
		super(key);
	}

	@Override
	public String parseValue(String configString) {
		return configString;
	}
}