package com.team766.library;

public interface SettableValueProvider<E> extends ValueProvider<E> {
	public void set(E value);

	public void clear();
}