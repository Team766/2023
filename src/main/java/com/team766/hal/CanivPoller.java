package com.team766.hal;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CanivPoller implements Runnable {
	private static final String CANIV_PATH = "/usr/bin";
	public static final String CANIV_BIN = CANIV_PATH + "/" + "caniv";
	private static final String CANIV_ARGS[] = { CANIV_BIN, "-a -i" }; // because, AI
	private static final int PROCESS_TIMEOUT_MILLIS = 2500;

	private final Executor threadPool;
	private final long periodMillis;
	private final ProcessBuilder processBuilder;
	private final AtomicBoolean done = new AtomicBoolean(false); 

	public CanivPoller(Executor threadPool, long periodMillis) {
		this.threadPool = threadPool;
		this.periodMillis = periodMillis;
		this.processBuilder = new ProcessBuilder(CANIV_ARGS);
	}

	@Override
	public void run() {
		while (!done.get()) {
			try {
				Process process = processBuilder.start();
				InputStream response = process.getInputStream();
				if (threadPool != null) {
					threadPool.execute(new CanivReader(response));
				}
				process.waitFor(PROCESS_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
				process.destroy();
				Thread.sleep(periodMillis); // TODO: measure execution time, adjust sleep.
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
