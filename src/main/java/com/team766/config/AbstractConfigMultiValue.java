package com.team766.config;

import java.lang.reflect.Array;
import java.util.function.IntFunction;
import org.json.JSONArray;

abstract class AbstractConfigMultiValue<E> extends AbstractConfigValue<E[]> {
	private final IntFunction<E[]> m_arrayFactory;

	@SuppressWarnings("unchecked")
	protected AbstractConfigMultiValue(String key, Class<E> elementClass) {
		super(key);
		m_arrayFactory = (int length) -> (E[])Array.newInstance(elementClass, length);
	}

	@Override
	protected final E[] parseJsonValue(Object configValue) {
		JSONArray jsonArray;
		try {
			jsonArray = (JSONArray)configValue;
		} catch (ClassCastException ex) {
			final E[] valueArray = m_arrayFactory.apply(1);
			valueArray[0] = parseJsonElement(configValue);
			return valueArray;
		}
		final int length = jsonArray.length();
		final E[] valueArray = m_arrayFactory.apply(length);
		for (int i = 0; i < length; ++i) {
			valueArray[i] = parseJsonElement(jsonArray.get(i));
		}
		return valueArray;
	}

	protected abstract E parseJsonElement(Object configElement);
}