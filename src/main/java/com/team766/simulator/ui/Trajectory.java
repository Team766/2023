package com.team766.simulator.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.team766.simulator.Parameters;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;

@SuppressWarnings("serial")
public class Trajectory extends JPanel {
	public static JFrame makePlotFrame(Collection<Double[]> series) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setContentPane(new Trajectory(series));
		frame.setVisible(true);
		return frame;
	}
	
	XYPlot plot;
	JSlider slider;
	DataTable data;
	JPanel plotPanel;
	Timer playbackTimer;
	
	public Trajectory(Collection<Double[]> series) {
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
		plot = new XYPlot(new DataSeries("Trajectory", data, 0, 1));
		plot.getAxis(XYPlot.AXIS_X).setRange(-10.0, 10.0);
		plot.getAxis(XYPlot.AXIS_Y).setRange(-10.0, 10.0);
		
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
		
		JPanel controlsPanel = new JPanel();
		controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.LINE_AXIS));
		slider = new JSlider(0, data.getRowCount() - 1);
		slider.setValue(0);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Trajectory.this.repaint();
			}
		});
		controlsPanel.add(slider);
		JButton playButton = new JButton(">");
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (playbackTimer.isRunning()) {
					playbackTimer.stop();
				} else {
					playbackTimer.start();
					if (slider.getValue() == slider.getMaximum()) {
						slider.setValue(slider.getMinimum());
					}
				}
			}
		});
		controlsPanel.add(playButton);
		add(controlsPanel, BorderLayout.SOUTH);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		int slider_value = slider.getValue();
		double x = (Double)data.get(0, slider_value);
		double y = (Double)data.get(1, slider_value);
		double orientation = (Double)data.get(2, slider_value);
		double vel_x = (Double)data.get(3, slider_value);
		double vel_y = (Double)data.get(4, slider_value);
		
		double pixelX = plot.getAxisRenderer(XYPlot.AXIS_X).worldToView(plot.getAxis(XYPlot.AXIS_X), x, false);
		double pixelY = plot.getAxisRenderer(XYPlot.AXIS_Y).worldToView(plot.getAxis(XYPlot.AXIS_Y), y, false);

		double plotAreaHeight = plot.getPlotArea().getHeight();
		double plotAreaX = plot.getPlotArea().getX();
		double plotAreaY = plot.getPlotArea().getY();
		
		pixelX = plotAreaX + pixelX;
		pixelY = plotAreaY + plotAreaHeight - pixelY;
		
		Graphics2D g2d = (Graphics2D)g;

		final int SIZE_X = 30;
		final int SIZE_Y = 20;
		g2d.setColor(new Color(128, 128, 255));
		AffineTransform saveXf = g2d.getTransform();
		g2d.rotate(-orientation, pixelX, pixelY);
		g2d.fillRect((int)pixelX - SIZE_X / 2, (int)pixelY - SIZE_Y / 2, SIZE_X, SIZE_Y);
		g2d.setTransform(saveXf);
		//g2d.setColor(Color.red);
		//g2d.drawLine((int)pixelX, (int)pixelY, (int)(pixelX + vel_x * 20), (int)(pixelY - vel_y * 20));
	}
}