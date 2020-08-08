package com.team766.library;

public interface ValueProvider<E> {
	public E get();
	
	public boolean hasValue();
}