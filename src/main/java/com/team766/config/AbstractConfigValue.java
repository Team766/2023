package com.team766.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.team766.library.ValueProvider;

public abstract class AbstractConfigValue<E> implements ValueProvider<E> {
	protected String m_key;
	private E m_cachedValue;
	private boolean m_cachedHasValue;
	private int m_cachedGeneration = -1;
	
	private static HashMap<String, AbstractConfigValue<?>> c_accessedValues = new HashMap<String, AbstractConfigValue<?>>();
	
	static Map<String, AbstractConfigValue<?>> accessedValues() {
		return Collections.unmodifiableMap(c_accessedValues);
	}
	
	protected AbstractConfigValue(String key) {
		m_key = key;
		c_accessedValues.put(key, this);
	}
	
	private void sync() {
		if (ConfigFileReader.instance.getGeneration() != m_cachedGeneration) {
			m_cachedGeneration = ConfigFileReader.instance.getGeneration();
			m_cachedHasValue = ConfigFileReader.instance.containsKey(m_key);
			if (m_cachedHasValue) {
				m_cachedValue = parseJsonValue(ConfigFileReader.instance.getRawValue(m_key));
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
	
	protected abstract E parseJsonValue(Object configValue);
	
	@Override
	public String toString() {
		String str = ConfigFileReader.instance.getRawValue(m_key).toString();
		if (str == null) {
			return "";
		} else {
			return str;
		}
	}
}