package com.team766.simulator.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;

import com.team766.simulator.Parameters;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;

@SuppressWarnings("serial")
public class Metrics extends JPanel {
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
	
	private static class Inspector extends MouseAdapter implements KeyListener {
		private int sourceIndex = 0;
		private double selectedTime = Double.NaN;
		private XYPlot plot;
		
		public Inspector(XYPlot plot) {
			this.plot = plot;
		}
		
		void update() {
			if (!Double.isNaN(selectedTime)) {
				DataSource source = plot.getData().get(sourceIndex);
				int index = Arrays.binarySearch(source.getColumn(0).toArray(null), selectedTime);
				if (index < 0) {
					index = -index - 1;
				}
				System.out.println(String.format("(%s, %f): %f", source.getName(), selectedTime, source.get(1, index)));
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			double x = e.getX() - plot.getPlotArea().getX();
			selectedTime = plot.getAxisRenderer(XYPlot.AXIS_X).viewToWorld(plot.getAxis(XYPlot.AXIS_X), x, false).doubleValue();
			
			update();
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
			if (e.getKeyChar() >= '1' && e.getKeyChar() <= '9') {
				int index = e.getKeyChar() - '1';
				if (index < plot.getData().size()) {
					sourceIndex = index;
					System.out.println("Selected " + plot.getData().get(sourceIndex).getName());
					update();
				}
			}
		}
		
		@Override
		public void keyReleased(KeyEvent e) {}
		
		@Override
		public void keyPressed(KeyEvent e) {}
	}
	
	public static JFrame makePlotFrame(Collection<Double[]> series, String[] labels) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setContentPane(new Metrics(series, labels));
		frame.setVisible(true);
		return frame;
	}
	
	XYPlot plot;
	JSlider slider;
	DataTable data;
	JPanel plotPanel;
	Timer playbackTimer;
	
	public Metrics(Collection<Double[]> series, String[] labels) {
		Double[] first = series.iterator().next();
		@SuppressWarnings("unchecked")
		Class<Double>[] types = new Class[first.length];
		Arrays.fill(types, Double.class);
		data = new DataTable(types);
		for (Double[] values : series) {
			if (first.length != values.length) {
				throw new IllegalArgumentException("Data values must be the same length");
			}
			data.add(values);
		}
		if (first.length - 1 != labels.length) {
			throw new IllegalArgumentException("Number of labels does not match the size of data values");
		}
		DataSource[] sources = new DataSource[labels.length];
		for (int i = 0; i < labels.length; ++i) {
			sources[i] = new DataSeries(labels[i], data, 0, i + 1);
		}
		plot = new XYPlot(sources);
		int colorIndex = 0;
		for (DataSource source : sources) {
			LineRenderer lines = new DefaultLineRenderer2D();
			plot.setLineRenderers(source, lines);
			Color color = Color.decode(COLORS[colorIndex++ % COLORS.length]);
			plot.getPointRenderers(source).get(0).setColor(color);
			plot.getLineRenderers(source).get(0).setColor(color);
		}
		plot.setLegendVisible(true);
		
		InteractivePanel panel = new InteractivePanel(plot);
		plotPanel = panel;
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		
		final int TIMER_PERIOD_MS = 50;
		playbackTimer = new Timer(TIMER_PERIOD_MS, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double deltaSteps = (TIMER_PERIOD_MS / 1000.0) / Parameters.TIME_STEP;
				int newValue = slider.getValue() + (int)deltaSteps;
				if (newValue > slider.getMaximum()) {
					newValue = slider.getMaximum();
					playbackTimer.stop();
				}
				slider.setValue(newValue);
			}
		});
		playbackTimer.setRepeats(true);
		
		Inspector inspector = new Inspector(plot);
		addKeyListener(inspector);
		panel.addMouseListener(inspector);
	}
}