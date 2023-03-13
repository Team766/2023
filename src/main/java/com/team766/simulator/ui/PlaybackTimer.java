package com.team766.simulator.ui;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.Timer;

/**
 * This provides a centralized tracking of playback time.
 *
 * Supports starting realtime playback, stopping playback, and seeking to a particular time.
 */
public class PlaybackTimer {
	// Minimum period between notifications to listeners when playback is running (in milliseconds).
	// Actual notification period may be longer than this, depending on how long it takes listeners
	// to run their callbacks.
	private static final int TIMER_PERIOD_MS = 50;

	// The property name used in the PropertyChangeEvents that this sends to listeners.
	public static final String PROPERTY_NAME = "playbackTime";

	private final Timer timer;

	private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	// The wall-clock time corresponding to t=0 of playback time.
	// Only valid when playback is running.
	private double startTime;
	// The playback time, when it was most recently updated.
	private double playTime = 0.0;
	// The playback time of the previous update.
	private double prevTime = 0.0;
	// The playback time at which playback should automatically stop.
	private final double endTime;
	// Monitor that synchronizes access to these *Time variables.
	private final Object timeLock = new Object();

	/**
	 * @param endTime The playback time at which playback should automatically stop.
	 */
	public PlaybackTimer(double endTime) {
		this.endTime = endTime;
		timer = new Timer(TIMER_PERIOD_MS, null) {
			@Override
			protected void fireActionPerformed(ActionEvent event) {
				synchronized (timeLock) {
					playTime = System.currentTimeMillis() * 0.001 - startTime;
					if (playTime >= endTime) {
						playTime = endTime;
						timer.stop();
					}
					super.fireActionPerformed(event);
					listeners.firePropertyChange(PROPERTY_NAME, prevTime, playTime);
					prevTime = playTime;
				}
			}
		};
		timer.setRepeats(true);
		timer.setCoalesce(true);
	}

	/**
	 * Register the given listener to receive callbacks when playback time updates.
	 */
	public void addListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	/**
	 * Returns whether playback is currently running.
	 */
	public boolean isRunning() {
		return timer.isRunning();
	}

	/**
	 * Returns the current playback time, as of the most recent update.
	 */
	public double currentTime() {
		return playTime;
	}

	/**
	 * Returns the playback time at which playback will automatically stop.
	 */
	public double endTime() {
		return endTime;
	}

	/**
	 * Start playback, if it is not already running.
	 * 
	 * If playback had previously reached endTime, which will automatically start again from the
	 * beginning.
	 */
	public void start() {
		if (isRunning()) {
			return;
		}
		synchronized(timeLock) {
			if (playTime >= endTime) {
				setTime(0.0);
			}
			startTime = System.currentTimeMillis() * 0.001 - playTime;
		}
		timer.restart();
	}

	/**
	 * Seek to the given playback time.
	 */
	public void setTime(double t) {
		if (t < 0.) {
			t = 0.;
		}
		synchronized(timeLock) {
			playTime = t;
			startTime = System.currentTimeMillis() * 0.001 - t;
			if (!isRunning()) {
				listeners.firePropertyChange(PROPERTY_NAME, prevTime, playTime);
			}
			prevTime = t;
		}
	}

	/**
	 * Stop playback.
	 */
	public void stop() {
		if (!isRunning()) {
			return;
		}
		timer.stop();
		synchronized(timeLock) {
			playTime = System.currentTimeMillis() * 0.001 - startTime;
		}
	}
}
