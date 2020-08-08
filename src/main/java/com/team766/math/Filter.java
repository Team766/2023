package com.team766.math;

public interface Filter {
	public void push(double sample);
	
	public double getValue();
}
