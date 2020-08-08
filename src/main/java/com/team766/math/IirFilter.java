package com.team766.math;

public class IirFilter implements Filter {
	private double decay;
	private double value;
	
	public IirFilter(double decay, double initialValue) {
		this.decay = decay;
		this.value = initialValue;
		if (decay > 1.0 || decay <= 0.0) {
			throw new IllegalArgumentException("decay should be in (0.0, 1.0]");
		}
	}
	
	public IirFilter(double decay) {
		this(decay, 0.0);
	}
	
	public void push(double sample) {
		value *= (1.0 - decay);
		value += sample * decay;
	}
	
	public double getValue() {
		return value;
	}
}
