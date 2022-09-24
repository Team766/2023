package com.team766.library;

public class ConstantValueProvider<E> implements ValueProvider<E> {
	private E m_value;
	
	public ConstantValueProvider(E value) {
		m_value = value;
	}

	public void set(E value) {
		m_value = value;
	}

	@Override
	public E get() {
		return m_value;
	}

	@Override
	public boolean hasValue() {
		return true;
	}
}
