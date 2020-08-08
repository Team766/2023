package com.team766.math;

/*import java.util.Arrays;

public class LinearInterpolation {
	private static class LerpArgs<E> {
		public E a;
		public E b;
		public double t;
	}
	
	private static <E> LerpArgs<E> getArgs(double[] t, E[] x, double q) {
		if (t.length != x.length) {
			throw new IllegalArgumentException("Keys and values must have the same length");
		}
		if (t.length == 0) {
			throw new IllegalArgumentException("Interpolated data must have at least one point");
		}
		LerpArgs<E> args = new LerpArgs<E>();
		int lower, upper;
		if (t.length == 1) {
			// 0-th order regression if we only have one point.
			lower = upper = 0;
			args.t = 0;
		} else {
			int index = Arrays.binarySearch(t, q);
			if (index >= 0) {
				// Exact match.
				lower = upper = index;
				args.t = 0;
			} else {
				upper = -index - 1;
				lower = upper - 1;
				double a_t = t[lower];
				double b_t = t[upper];
				args.t = (q - a_t) / (b_t - a_t);
			}
		}
		args.a = x[lower];
		args.b = x[upper];
		return args;
	}
	
	public static double get(double[] t, Double[] x, double q) {
		LerpArgs<Double> args = getArgs(t, x, q);
		return args.a * (1 - args.t) + (args.b * args.t);
	}
	
	public static <E extends Algebraic<E>> E get(double[] t, E[] x, double q) {
		LerpArgs<E> args = getArgs(t, x, q);
		return args.a.scale(1 - args.t).add(args.b.scale(args.t));
	}
}*/