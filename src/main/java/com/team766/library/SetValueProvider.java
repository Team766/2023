package com.team766.library;

public class SetValueProvider<E> implements SettableValueProvider<E> {
	private E m_value;
	private boolean m_hasValue;

	public SetValueProvider() {
		m_value = null;
		m_hasValue = false;
	}

	public SetValueProvider(E value) {
		m_value = value;
		m_hasValue = true;
	}

	@Override
	public E get() {
		return m_value;
	}

	@Override
	public boolean hasValue() {
		return m_hasValue;
	}

	public void set(E value) {
		m_value = value;
		m_hasValue = true;
	}

	public void clear() {
		m_value = null;
		m_hasValue = false;
	}
}
