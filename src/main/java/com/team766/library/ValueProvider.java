package com.team766.library;

public interface ValueProvider<E> {
	public E get();
	
	public boolean hasValue();

	default E valueOr(E default_value) {
		if (hasValue()) {
			return get();
		}
		return default_value;
	}
}