package com.team766.simulator.ui;

import java.awt.Point;
import java.util.Arrays;
import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;

/**
 * Various utility methods for dealing with the Gral plotting library.
 */
class PlotUtils {
	/**
	 * Return a DataTable containing the data in the given series of multivariate data.
	 * 
	 * @throws IllegalArgumentException if all elements of `series` are not of the same length.
	 */
	public static DataTable makeDataTable(Iterable<Double[]> series) {
		Double[] first = series.iterator().next();
		@SuppressWarnings("unchecked")
		Class<Double>[] types = new Class[first.length];
		Arrays.fill(types, Double.class);
		DataTable data = new DataTable(types);
		for (Double[] values : series) {
			if (first.length != values.length) {
				throw new IllegalArgumentException("Data values must be the same length");
			}
			data.add(values);
		}
		return data;
	}

	/**
	 * Return the index of the first element which is greater than or equal to `value`.
	 * If no such element is found, returns the index of the last element.
	 */
	public static <T> int findIndex(Column column, Comparable<T> value) {
		// TODO: We use this to search sorted data, so we ought to be able to use a binary search,
		// but Column is not compatible with either Arrays.binarySearch or Collections.binarySearch.
		// NOTE: Calling column.toArray() and then Arrays.binarySearch would be more expensive than
		// this linear scan.
		int index = 0;
		for (Object v : column) {
			@SuppressWarnings("unchecked")
			var t_v = (T)v;
			if (value.compareTo(t_v) < 0) {
				return index;
			}
			++index;
		}
		return index - 1;
	}

	/**
	 * Convert a (x, y) coordinate pair from data values to UI coordinates.
	 * 
	 * This is useful for drawing overlays onto plots.
	 */
	public static Point getPixelCoords(XYPlot plot, double x, double y) {
		double pixelX = plot.getAxisRenderer(XYPlot.AXIS_X).worldToView(plot.getAxis(XYPlot.AXIS_X), x, false);
		double pixelY = plot.getAxisRenderer(XYPlot.AXIS_Y).worldToView(plot.getAxis(XYPlot.AXIS_Y), y, false);

		double plotAreaHeight = plot.getPlotArea().getHeight();
		double plotAreaX = plot.getPlotArea().getX();
		double plotAreaY = plot.getPlotArea().getY();

		pixelX = plotAreaX + pixelX;
		pixelY = plotAreaY + plotAreaHeight - pixelY;

		return new Point((int)pixelX, (int)pixelY);
	}
}
