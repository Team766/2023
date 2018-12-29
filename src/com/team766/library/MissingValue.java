package com.team766.library;

public class MissingValue<E> implements ValueProvider<E> {

	@Override
	public E get() {
		return null;
	}

	@Override
	public boolean hasValue() {
		return false;
	}

}
