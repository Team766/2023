package com.team766.config;

class DoubleConfigValue extends AbstractConfigValue<Double> {
	protected DoubleConfigValue(String key) {
		super(key);
	}

	@Override
	public Double parseJsonValue(Object configValue) {
		return ((Number)configValue).doubleValue();
	}
}

class IntegerConfigValue extends AbstractConfigValue<Integer> {
	protected IntegerConfigValue(String key) {
		super(key);
	}

	@Override
	public Integer parseJsonValue(Object configValue) {
		return ((Number)configValue).intValue();
	}
}

class DoubleConfigMultiValue extends AbstractConfigMultiValue<Double> {
	protected DoubleConfigMultiValue(String key) {
		super(key, Double.class);
	}

	@Override
	public Double parseJsonElement(Object configElement) {
		return ((Number)configElement).doubleValue();
	}
}

class IntegerConfigMultiValue extends AbstractConfigMultiValue<Integer> {
	protected IntegerConfigMultiValue(String key) {
		super(key, Integer.class);
	}

	@Override
	public Integer parseJsonElement(Object configElement) {
		return ((Number)configElement).intValue();
	}
}

class BooleanConfigValue extends AbstractConfigValue<Boolean> {
	protected BooleanConfigValue(String key) {
		super(key);
	}

	@Override
	public Boolean parseJsonValue(Object configValue) {
		return (Boolean)configValue;
	}
}

class StringConfigValue extends AbstractConfigValue<String> {
	protected StringConfigValue(String key) {
		super(key);
	}

	@Override
	public String parseJsonValue(Object configValue) {
		return (String)configValue;
	}
}