package com.team766.hal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CanivPoller implements Runnable {
	private static final String CANIV_PATH = "/usr/local/bin";
	public static final String CANIV_BIN = CANIV_PATH + "/" + "caniv";
	private static final String CANIV_ARGS[] = { CANIV_BIN, "--list" };

	private final long periodMillis;
	private final ProcessBuilder processBuilder;
	private final AtomicBoolean done = new AtomicBoolean(false); 

	public CanivPoller(long periodMillis) {
		this.periodMillis = periodMillis;
		this.processBuilder = new ProcessBuilder(CANIV_ARGS);
	}

	@Override
	public void run() {
		while (!done.get()) {
			try {
				Process process = processBuilder.start();
				InputStream response = process.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(response));
				List<String> lines = new ArrayList<String>();
				String line = null;
				while ((line = reader.readLine()) != null) {
					lines.add(line);
				}
				SmartDashboard.putStringArray("caniv", lines.toArray(new String[0]));
				reader.close();
				Thread.sleep(periodMillis); // TODO: take execution time into account too?
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public void setDone(boolean done) {
		this.done.set(done);
	}
}
