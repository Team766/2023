package com.team766.config;

import java.util.Arrays;

import com.team766.library.ValueProvider;

abstract class AbstractConfigValue<E> implements ValueProvider<E> {
	protected String m_key;
	private E m_cachedValue;
	private boolean m_cachedHasValue;
	private int m_cachedGeneration = -1;
	
	protected AbstractConfigValue(String key) {
		m_key = key;
	}
	
	private void sync() {
		if (ConfigFileReader.instance.getGeneration() != m_cachedGeneration) {
			m_cachedGeneration = ConfigFileReader.instance.getGeneration();
			m_cachedHasValue = ConfigFileReader.instance.containsKey(m_key);
			if (m_cachedHasValue) {
				m_cachedValue = loadValue();
			} else {
				m_cachedValue = null;
			}
		}
	}
	
	@Override
	public boolean hasValue() {
		sync();
		return m_cachedHasValue;
	}
	
	@Override
	public E get() {
		sync();
		if (!m_cachedHasValue) {
			throw new IllegalArgumentException(m_key + " not found in the config file");
		}
		return m_cachedValue;
	}
	
	protected abstract E loadValue();
	
	protected String loadString() {
		return ConfigFileReader.instance.getRawString(m_key);
	} 
}

abstract class AbstractConfigMultiValue<E> extends AbstractConfigValue<E[]> {
	protected AbstractConfigMultiValue(String key) {
		super(key);
	}

	protected String[] loadStrings() {
		return loadString().split(",");
	}
}

class DoubleConfigValue extends AbstractConfigValue<Double> {
	protected DoubleConfigValue(String key) {
		super(key);
	}

	@Override
	protected Double loadValue() {
		return Double.valueOf(loadString());
	}
}

class IntegerConfigValue extends AbstractConfigValue<Integer> {
	protected IntegerConfigValue(String key) {
		super(key);
	}

	@Override
	protected Integer loadValue() {
		return Integer.valueOf(loadString());
	}
}

class DoubleConfigMultiValue extends AbstractConfigMultiValue<Double> {
	protected DoubleConfigMultiValue(String key) {
		super(key);
	}

	@Override
	protected Double[] loadValue() {
		return (Double[]) Arrays.stream(loadStrings()).map(Double::valueOf).toArray((size) -> new Double[size]);
	}
}

class IntegerConfigMultiValue extends AbstractConfigMultiValue<Integer> {
	protected IntegerConfigMultiValue(String key) {
		super(key);
	}

	@Override
	protected Integer[] loadValue() {
		return (Integer[]) Arrays.stream(loadStrings()).map(Integer::valueOf).toArray((size) -> new Integer[size]);
	}
}

class BooleanConfigValue extends AbstractConfigValue<Boolean> {
	protected BooleanConfigValue(String key) {
		super(key);
	}

	@Override
	protected Boolean loadValue() {
		return Boolean.valueOf(loadString());
	}
}

class StringConfigValue extends AbstractConfigValue<String> {
	protected StringConfigValue(String key) {
		super(key);
	}

	@Override
	protected String loadValue() {
		return loadString();
	}
}