package com.team766.simulator.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;

/**
 * UI Window that displays the movements that the robot's arm made.
 */
public class ArmTrajectory extends JPanel {
	private final XYPlot plot;
	private final DataTable data;
	// The current playback time.
	private double time;

	/**
	 * @param series The time series data defining the robot's trajectory.
	 *     Each element should be an array with 5 values:
	 *      [0]: Time
	 *      [1]: X Position of the end of the first link
	 *      [2]: Y Position of the end of the first link
	 *      [3]: X Position of the end of the second link
	 *      [4]: Y Position of the end of the second link
	 * @param playbackTimer The timer (shared between windows) which controls playback time.
	 */
	public ArmTrajectory(Iterable<Double[]> series, PlaybackTimer playbackTimer) {
		// Create an X-Y plot to display the trajectories
		data = PlotUtils.makeDataTable(series);
		var firstTrajectory = new DataSeries("First link", data, 1, 2);
		var secondTrajectory = new DataSeries("Second link", data, 3, 4);
		plot = new XYPlot(firstTrajectory, secondTrajectory);
		plot.getAxis(XYPlot.AXIS_X).setRange(-2.5, 2.5);
		plot.getAxis(XYPlot.AXIS_Y).setRange(-2.5, 2.5);

		// Show the trajectories for the arm's links using different colors.
		{
			LineRenderer lines = new DefaultLineRenderer2D();
			lines.setColor(new Color(0, 0, 128));
			plot.setLineRenderers(firstTrajectory, lines);
			plot.setPointRenderers(firstTrajectory, null);
		}
		{
			LineRenderer lines = new DefaultLineRenderer2D();
			lines.setColor(new Color(128, 0, 0));
			plot.setLineRenderers(secondTrajectory, lines);
			plot.setPointRenderers(secondTrajectory, null);
		}
		
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

		double j1_x = (Double)data.get(1, index);
		double j1_y = (Double)data.get(2, index);
		double j2_x = (Double)data.get(3, index);
		double j2_y = (Double)data.get(4, index);

		// Show the current position of the arm by drawing a line for each of the links.

		Point j0_p = PlotUtils.getPixelCoords(plot, 0.0, 0.0);
		Point j1_p = PlotUtils.getPixelCoords(plot, j1_x, j1_y);
		Point j2_p = PlotUtils.getPixelCoords(plot, j2_x, j2_y);

		g2d.setStroke(new BasicStroke(10));
		g2d.setColor(new Color(128, 128, 255));
		g2d.drawLine(j0_p.x, j0_p.y, j1_p.x, j1_p.y);
		g2d.setColor(Color.red);
		g2d.drawLine(j1_p.x, j1_p.y, j2_p.x, j2_p.y);
	}
}