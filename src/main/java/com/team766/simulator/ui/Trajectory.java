package com.team766.simulator.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;

/**
 * UI Window that displays the path that the robot drove.
 */
public class Trajectory extends JPanel {
	private final XYPlot plot;
	private final DataTable data;
	// The current playback time.
	private double time;

	/**
	 * @param series The time series data defining the robot's trajectory.
	 *     Each element should be an array with 6 values:
	 *      [0]: Time
	 *      [1]: X Position
	 *      [2]: Y Position
	 *      [3]: Orientation
	 *      [4]: X Velocity
	 *      [5]: Y Velocity
	 * @param playbackTimer The timer (shared between windows) which controls playback time.
	 */
	public Trajectory(Iterable<Double[]> series, PlaybackTimer playbackTimer) {
		// Create an X-Y plot to display the trajectory
		data = PlotUtils.makeDataTable(series);
		var source = new DataSeries("Trajectory", data, 1, 2);
		plot = new XYPlot(source);
		plot.getAxis(XYPlot.AXIS_X).setRange(-10.0, 10.0);
		plot.getAxis(XYPlot.AXIS_Y).setRange(-10.0, 10.0);
		plot.setPointRenderers(source, null);
		
		InteractivePanel panel = new InteractivePanel(plot);
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);

		// Add the standard time slider and play/pause button.
		add(new PlaybackControls(playbackTimer), BorderLayout.SOUTH);

		// Add the callback that will update this window when playback time progresses.
		playbackTimer.addListener(event -> {
			this.time = (Double)event.getNewValue();
			this.repaint();
		});
	}

	/**
	 * Draw overlays on the plot
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D)g;

		int index = PlotUtils.findIndex(data.getColumn(0), time);

		double x = (Double)data.get(1, index);
		double y = (Double)data.get(2, index);
		double orientation = (Double)data.get(3, index);
		double vel_x = (Double)data.get(4, index);
		double vel_y = (Double)data.get(5, index);

		// Show the robot's current pose by drawing a rectangle.
		Point pixelPos = PlotUtils.getPixelCoords(plot, x, y);
		final int SIZE_X = 30;
		final int SIZE_Y = 20;
		g2d.setColor(new Color(128, 128, 255));
		AffineTransform saveXf = g2d.getTransform();
		g2d.rotate(-orientation, pixelPos.x, pixelPos.y);
		g2d.fillRect((int)pixelPos.x - SIZE_X / 2, (int)pixelPos.y - SIZE_Y / 2, SIZE_X, SIZE_Y);
		g2d.setTransform(saveXf);

		// Show the robot's current velocity by drawing a line extending from the robot's position.
		//g2d.setColor(Color.red);
		//g2d.drawLine((int)pixelPos.x, (int)pixelPos.y, (int)(pixelPos.x + vel_x * 20), (int)(pixelPos.y - vel_y * 20));
	}
}