package com.team766.math;

public interface Algebraic<E extends Algebraic<E>> {
	public E add(E b);
	
	public E scale(double b);
}
