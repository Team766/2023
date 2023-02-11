package com.team766.simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores a set of parallel time series (i.e. each series has data at the same points in time).
 */
public class Metrics {
	/**
	 * This is a handle to a particular time series.
	 */
	public class Series {
		private final int index;

		private Series(int index) {
			this.index = index;
		}

		private void check(Metrics m) {
			if (m != Metrics.this) {
				throw new IllegalArgumentException();
			}
		}
	}

	/**
	 * This is a view on a point in time, with the data for all of the time series at that point.
	 */
	public class Point {
		private final Double[] data;

		private Point(Double[] data) {
			this.data = data;
		}

		/**
		 * Set the value in a particular series at this point in time.
		 */
		public Point set(Series series, double value) {
			series.check(Metrics.this);
			if (series.index >= 0) {
				data[series.index] = value;
			}
			return this;
		}
	}

	// Each element of this list should be an array of the same length.
	// In each element, index 0 is time and subsequent elements are values.
	private ArrayList<Double[]> metrics = new ArrayList<>();
	// The labels for the series.
	private ArrayList<String> seriesLabels = new ArrayList<>();

	/**
	 * Add a new data series.
	 * 
	 * This can only be called before data is added.
	 *
	 * @param name The label for the series.
	 * @param enabled If false, data from this series will be ignored. This allows for an easy,
	 *     centralized way of selecting which data should be processed/visualized.
	 * @return A handle to this series, which should be passed when adding data via a Point.
	 * @throws IllegalStateException if called after data has been added (via {@link #add()})
	 */
	public Series addSeries(String name, boolean enabled) {
		if (metrics.size() != 0) {
			throw new IllegalStateException();
		}
		if (!enabled) {
			return new Series(-1);
		}
		seriesLabels.add(name);
		return new Series(seriesLabels.size());
	}
	public Series addSeries(String name) {
		return addSeries(name, true);
	}

	/**
	 * Add a new data point.
	 *
	 * Data points should be added in order of increasing time.
	 */
	public Point add(double time) {
		var data = new Double[seriesLabels.size() + 1];
		data[0] = time;
		metrics.add(data);
		return new Point(data);
	}

	/**
	 * Remove all data points (Series are not removed)
	 */
	public void clear() {
		metrics.clear();
	}

	public List<Double[]> getMetrics() {
		return Collections.unmodifiableList(metrics);
	}

	public List<String> getLabels() {
		return Collections.unmodifiableList(seriesLabels);
	}
}
