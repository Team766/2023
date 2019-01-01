package com.team766.math;

public class FirFilter implements Filter {
	private double[] buffer;
	private int count = 0;
	private int headIndex = 0;
	
	public FirFilter(int bufferLength) {
		buffer = new double[bufferLength];
	}
	
	public void push(double sample) {
		buffer[headIndex] = sample;
		headIndex = (headIndex + 1) % buffer.length;
		if (count < buffer.length) {
			++count;
		}
	}
	
	public double getValue() {
		double accum = 0.0;
		for (int i = 0; i < count; ++i) {
			accum += buffer[i] / count;
		}
		return accum;
	}
}
