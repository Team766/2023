package com.team766.simulator.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import com.team766.simulator.Metrics;
import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;

/**
 * UI Window that displays plots of time-series data.
 */
public class MetricsPlot extends JPanel {
	// Color palette from http://www.mulinblog.com/a-color-palette-optimized-for-data-visualization/
	private static final String[] COLORS = {
			"#4D4D4D", // gray
			"#5DA5DA", // blue
			"#FAA43A", // orange
			"#60BD68", // green
			"#F17CB0", // pink
			"#B2912F", // brown
			"#B276B2", // purple
			"#DECF3F", // yellow
			"#F15854", // red
		};

	private static class DataSeriesWithMutableName extends DataSeries {
		public DataSeriesWithMutableName(String name, DataSource data, int... cols) {
			super(name, data, cols);
		}

		// Override to make this public
		@Override
		public void setName(String name) {
			super.setName(name);
		}
	}

	private final XYPlot plot;
	// The data store. Column 0 is time, the other columns are the metrics values.
	private final DataTable data;
	// A view into data for each of the series.
	private final DataSeriesWithMutableName[] sources;
	// A name for each of the series.
	private final List<String> labels;
	// The current playback time.
	private double time;

	/**
	 * @param metrics The time series data to display
	 * @param playbackTimer The timer (shared between windows) which controls playback time.
	 */
	public MetricsPlot(Metrics metrics, PlaybackTimer playbackTimer) {
		// Create the plot
		data = PlotUtils.makeDataTable(metrics.getMetrics());
		labels = metrics.getLabels();
		sources = new DataSeriesWithMutableName[labels.size()];
		for (int i = 0; i < labels.size(); ++i) {
			sources[i] = new DataSeriesWithMutableName(labels.get(i), data, 0, i + 1);
		}
		plot = new XYPlot(sources);

		// Add a little margin on all sides of the plot for better readability. Also make sure
		// that the axes are visible so we can see the scale of the data.
		var xAxis = plot.getAxis(XYPlot.AXIS_X);
		xAxis.setMin(-0.05 * xAxis.getRange());
		xAxis.setMax(xAxis.getMax().doubleValue() + 0.05 * xAxis.getRange());
		var yAxis = plot.getAxis(XYPlot.AXIS_Y);
		yAxis.setMin(Math.min(yAxis.getMin().doubleValue() - 0.05 * yAxis.getRange(), -0.05 * yAxis.getRange()));
		yAxis.setMax(Math.max(yAxis.getMax().doubleValue() + 0.05 * yAxis.getRange(), 0.05 * yAxis.getRange()));

		// Assign a different color to each data series.
		int colorIndex = 0;
		for (DataSource source : sources) {
			LineRenderer lines = new DefaultLineRenderer2D();
			Color color = Color.decode(COLORS[colorIndex++ % COLORS.length]);
			lines.setColor(color);
			plot.setLineRenderers(source, lines);
			plot.setPointRenderers(source, null);
		}

		// Setup the legend so it's visible and placed on the top-right
		updateLegend();
		plot.getLegend().setAlignmentX(1.0);
		plot.getLegend().setAlignmentY(0.1);
		plot.setLegendVisible(true);

		InteractivePanel panel = new InteractivePanel(plot);
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);

		// Remove listener installed by InteractivePanel that conflicts with our MouseListener.
		for (var l : panel.getMouseListeners()) {
			// We're looking to remove "MouseZoomListener", which is a private inner class, and Java
			// doesn't seem to have its proper name stored in reflection (the stored name is "a").
			// We instead find it because it's the only one of the listeners which directly
			// implements MouseWheelListener (note that other of the listeners extend MouseAdapter,
			// meaning they indirectly implement MouseWheelListener, so we can't just use instanceof).
			if (Arrays.asList(l.getClass().getInterfaces()).contains(MouseWheelListener.class)) {
				panel.removeMouseListener(l);
			}
		}
		// Add a mouse listener that will set playback time if the plot is double-clicked.
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!SwingUtilities.isLeftMouseButton(e) || e.getClickCount() < 2) {
					return;
				}

				double x = e.getX() - plot.getPlotArea().getX();
				double selectedTime = plot.getAxisRenderer(XYPlot.AXIS_X).viewToWorld(plot.getAxis(XYPlot.AXIS_X), x, false).doubleValue();

				playbackTimer.setTime(selectedTime);

				// final int index = PlotUtils.findIndex(data.getColumn(0), time);
				// System.out.println("At time=" + selectedTime + ":");
				// for (int i = 0; i < labels.size(); ++i) {
				// 	System.out.println("  " + labels.get(i) + ": " + data.get(i + 1, index));
				// }
				// System.out.println();

				e.consume();
			}
		});

		// Add the standard time slider and play/pause button.
		add(new PlaybackControls(playbackTimer), BorderLayout.SOUTH);

		// Add the callback that will update this window when playback time progresses.
		playbackTimer.addListener(event -> {
			this.time = (Double)event.getNewValue();
			updateLegend();
			this.repaint();
		});
	}

	/**
	 * Update the legend so that it displays the current value for each of the series.
	 */
	private void updateLegend() {
		final int index = PlotUtils.findIndex(data.getColumn(0), time);
		final var legend = plot.getLegend();
		legend.clear();
		for (int i = 0; i < sources.length; ++i) {
			sources[i].setName(String.format("%s: %.4f", labels.get(i), data.get(i + 1, index)));
			legend.add(sources[i]);
		}
	}

	/**
	 * Draw overlays on the plot
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D g2d = (Graphics2D)g;
		
		// Draw a dashed vertical line to trace the current time on the plot.
		final int lineX = PlotUtils.getPixelCoords(plot, time, 0.0).x;
		final double plotAreaTop = plot.getPlotArea().getY();
		final double plotAreaBottom = plotAreaTop + plot.getPlotArea().getHeight();
		g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.f, new float[]{10.0f}, 0.f));
		g2d.setColor(Color.black);
		g2d.drawLine(lineX, (int)plotAreaTop, lineX, (int)plotAreaBottom);
	}
}