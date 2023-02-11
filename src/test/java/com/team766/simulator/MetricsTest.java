package com.team766.simulator;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MetricsTest {
	@Test
	public void test() {
		var metrics = new Metrics();
		var series1 = metrics.addSeries("Series1");
		var series2 = metrics.addSeries("Series2");

		metrics.add(0).set(series1, 42).set(series2, 24);
		metrics.add(1).set(series1, 83).set(series2, 38);
		
		Assertions.assertIterableEquals(metrics.getLabels(), List.of("Series1", "Series2"));
		Assertions.assertArrayEquals(metrics.getMetrics().toArray(), new Double[][]{new Double[]{0., 42., 24.}, new Double[]{1., 83., 38.}});
	}
}