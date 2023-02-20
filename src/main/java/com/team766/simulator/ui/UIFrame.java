package com.team766.simulator.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.prefs.Preferences;
import javax.swing.JFrame;

/**
 * A subclass of JFrame that adds automatic saving/restoring of the window's position and size.
 * 
 * We open several windows to display plots and trajectories, and it's preferable to not have to
 * rearrange them everytime the simulation is restarted.
 */
public class UIFrame extends JFrame {
	private Preferences prefs;

	public UIFrame(String frameUniqueId, Container content) {
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setSize(800, 600);
		super.setContentPane(content);
		super.setTitle(frameUniqueId);
		super.setVisible(true);

		prefs = Preferences.userNodeForPackage(UIFrame.class).node(frameUniqueId);

		restoreFrameLocation();
		restoreFrameSize();

		super.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updatePref();
			}
	
			@Override
			public void componentMoved(ComponentEvent e) {
				updatePref();
			}
		});
	}
	
	private void updatePref() {
		Point location = super.getLocation();
		prefs.putInt("x", location.x);
		prefs.putInt("y", location.y);
		Dimension size = super.getSize();
		prefs.putInt("w", size.width);
		prefs.putInt("h", size.height);
	}
	
	private void restoreFrameSize() {
		int w = prefs.getInt("w", -1);
		int h = prefs.getInt("h", -1);
		if (w < 0 || h < 0) {
			return;
		}
		super.setSize(new Dimension(w, h));
	}
	
	private void restoreFrameLocation() {
		int x = prefs.getInt("x", -1);
		int y = prefs.getInt("y", -1);
		if (x < 0 || y < 0) {
			return;
		}
		super.setLocation(new Point(x, y));
	}
}
