package com.team766.hal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringPublisher;

public class CanivPoller implements Runnable {
	private static final String CANIV_PATH = "/usr/bin";
	public static final String CANIV_BIN = CANIV_PATH + "/" + "caniv";
	private static final String CANIV_ARGS[] = { CANIV_BIN, "-a", "-i" }; // because, AI

	private final Logger logger = Logger.get(Category.DRIVE);

	private final long periodMillis;
	private final ProcessBuilder processBuilder;
	private final AtomicBoolean done = new AtomicBoolean(false);
	private final NetworkTableInstance nti = NetworkTableInstance.getDefault();
	private final NetworkTable table = nti.getTable("caniv");
	private final Map<String,StringPublisher> publishers = new HashMap<String,StringPublisher>();

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
				logger.logRaw(Severity.INFO, "caniv");
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (line.isEmpty()) continue;
					logger.logRaw(Severity.INFO, line);
					String[] keyValue = line.split(":", 2);
					if (keyValue.length != 2) continue;
					keyValue[0] = keyValue[0].trim();
					keyValue[1] = keyValue[1].trim();

					if (!publishers.containsKey(keyValue[0])) {
						publishers.put(keyValue[0], table.getStringTopic(keyValue[0]).publish());
					}
					publishers.get(keyValue[0]).set(keyValue[1]);
				}
				response.close();

				process.destroy();

			} catch (Exception e) {
				logger.logRaw(Severity.ERROR, "Exception caught trying to execute or parse output from caniv: " 
				  + e.getMessage());
				e.printStackTrace();
			}

			try {
				Thread.sleep(periodMillis); // TODO: measure execution time, adjust sleep.
			} catch (Exception e) {
				logger.logRaw(Severity.ERROR, "Exception caught trying to sleep: " 
				+ e.getMessage());
			  	e.printStackTrace();	
			}
		}
	}

	public void setDone(boolean done) {
		this.done.set(done);
	}
}
