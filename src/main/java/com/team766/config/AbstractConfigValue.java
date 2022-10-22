package com.team766.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import com.team766.library.ValueProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;

public abstract class AbstractConfigValue<E> implements ValueProvider<E> {
	protected String m_key;
	private E m_cachedValue;
	private boolean m_cachedHasValue;
	private int m_cachedGeneration = -1;
	
	private static ArrayList<AbstractConfigValue<?>> c_accessedValues = new ArrayList<AbstractConfigValue<?>>();
	
	static Collection<AbstractConfigValue<?>> accessedValues() {
		return Collections.unmodifiableCollection(c_accessedValues);
	}

	static void resetStatics() {
		c_accessedValues.clear();
	}
	
	protected AbstractConfigValue(String key) {
		m_key = key;
		c_accessedValues.add(this);
		// Querying for this config setting's key will add a placeholder entry
		// in the config file if this setting does not already exist there.
		ConfigFileReader.instance.getRawValue(m_key);
	}
	
	private void sync() {
		if (ConfigFileReader.instance.getGeneration() != m_cachedGeneration) {
			m_cachedGeneration = ConfigFileReader.instance.getGeneration();
			var rawValue = ConfigFileReader.instance.getRawValue(m_key);
			m_cachedHasValue = rawValue != null;
			if (m_cachedHasValue) {
				try {
					m_cachedValue = parseJsonValue(rawValue);
				} catch (Exception ex) {
					Logger.get(Category.CONFIGURATION).logRaw(Severity.ERROR, "Failed to parse " + m_key + " from the config file: " + LoggerExceptionUtils.exceptionToString(ex));
					m_cachedValue = null;
					m_cachedHasValue = false;
				}
			}
		}
	}

	public String getKey() {
		return m_key;
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
		sync();
		if (!m_cachedHasValue) {
			return "<unset>";
		}
		if (m_cachedValue == null) {
			return "<null>";
		}
		return m_cachedValue.toString();
	}
}