package com.team766.logging;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LoggerTest {
	@Test
	public void test() throws IOException, InterruptedException {
		TemporaryFolder workingDir = new TemporaryFolder();
		workingDir.create();
		try {
			String logFile = new File(workingDir.getRoot(), "test.log").getPath();
			LogWriter writer = new LogWriter(logFile);
			writer.log(Severity.ERROR, Category.AUTONOMOUS, "num: %d str: %s", 42, "my string");
			writer.log(Severity.ERROR, Category.AUTONOMOUS, "num: %d str: %s", 63, "second blurb");
			writer.logRaw(Severity.WARNING, Category.AUTONOMOUS, "Test raw log");
			writer.close();
			
			LogReader reader = new LogReader(logFile);
			String logString1 = reader.readNext().format(reader);
			assertEquals("num: 42 str: my string", logString1);
			String logString2 = reader.readNext().format(reader);
			assertEquals("num: 63 str: second blurb", logString2);
			String logString3 = reader.readNext().format(reader);
			assertEquals("Test raw log", logString3);
		} finally {
			workingDir.delete();
		}
	}

	@Test
	public void stressTest() throws IOException, InterruptedException {
		final long NUM_THREADS = 8;
		final long RUN_TIME_SECONDS = 3;

		LogWriter writer = new LogWriter(OutputStream.nullOutputStream());
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for (int j = 0; j < NUM_THREADS; ++j) {
			Thread t = new Thread(() ->{
				long end = System.currentTimeMillis() + RUN_TIME_SECONDS * 1000;
				while (System.currentTimeMillis() < end) {
					writer.log(Severity.ERROR, Category.AUTONOMOUS, "num: %d str: %s", 42, "my string");
					writer.logRaw(Severity.WARNING, Category.AUTONOMOUS, "Test raw log");
				}
			});
			t.start();
			threads.add(t);
		}
		for (Thread t : threads) {
			t.join();
		}
		writer.close();
	}
}