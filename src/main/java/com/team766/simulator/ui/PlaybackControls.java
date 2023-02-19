package com.team766.simulator.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A panel included at the bottom of all of the UI windows that allows for controlling time during
 * playback.
 */
public class PlaybackControls extends JPanel {
	// How large one step of the slider is, in seconds.
	private static final double SLIDER_RESOLUTION = 0.020;

	// There's a circular dependency between the PlaybackTimer and the slider: both have the ability
	// to change time, and both are updated when the other changes time, triggering callbacks to
	// listeners.
	// This flag is set to true during the callback that is executed when the PlaybackTimer updates
	// its time, so that when it updates the position of the slider, the slider's callback will not
	// try to update timer, which would lead to an infinite loop of callbacks.
	private boolean inTimerTick = false;

	/**
	 * @param playbackTimer The timer (shared between windows) which controls playback time.
	 */
	public PlaybackControls(PlaybackTimer playbackTimer) {
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		// Add a slider that shows the progression of time and also allows for seeking to a
		// different time.
		var slider = new JSlider(0, (int)Math.ceil(playbackTimer.endTime() / SLIDER_RESOLUTION));
		slider.setValue(0);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (inTimerTick) {
					return;
				}
				playbackTimer.setTime(slider.getValue() * SLIDER_RESOLUTION);
			}
		});
		add(slider);

		// Add a button that can start and stop playback.
		JButton playButton = new JButton(">");
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (playbackTimer.isRunning()) {
					playbackTimer.stop();
				} else {
					playbackTimer.start();
				}
			}
		});
		add(playButton);

		// Add the callback that will update this panel when playback time progresses.
		playbackTimer.addListener((PropertyChangeEvent event) -> {
			inTimerTick = true;
			slider.setValue((int)Math.ceil((Double)event.getNewValue() / SLIDER_RESOLUTION));
			inTimerTick = false;
		});
	}
}
