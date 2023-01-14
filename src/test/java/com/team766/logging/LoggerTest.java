package com.team766.logging;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LoggerTest {
	@Rule
  	public TemporaryFolder workingDir = new TemporaryFolder();

	@Test
	public void test() throws IOException, InterruptedException {
		String logFile = new File(workingDir.getRoot(), "test.log").getPath();
		LogWriter writer = new LogWriter(logFile);
		writer.logStoredFormat(
			LogEntry.newBuilder()
				.setTime(Logger.getTime())
				.setSeverity(Severity.ERROR)
				.setCategory(Category.AUTONOMOUS)
				.setMessageStr("num: %d str: %s")
				.addArg(LogValue.newBuilder().setIntValue(42))
				.addArg(LogValue.newBuilder().setStringValue("my string")));
		writer.logStoredFormat(
			LogEntry.newBuilder()
				.setTime(Logger.getTime())
				.setSeverity(Severity.ERROR)
				.setCategory(Category.AUTONOMOUS)
				.setMessageStr("num: %d str: %s")
				.addArg(LogValue.newBuilder().setIntValue(63))
				.addArg(LogValue.newBuilder().setStringValue("second blurb")));
		writer.log(
			LogEntry.newBuilder()
				.setTime(Logger.getTime())
				.setSeverity(Severity.WARNING)
				.setCategory(Category.AUTONOMOUS)
				.setMessageStr("Test raw log")
				.build());
		writer.close();
		
		LogReader reader = new LogReader(logFile);
		String logString1 = LogEntryRenderer.renderLogEntry(reader.readNext(), reader);
		assertEquals("num: 42 str: my string", logString1);
		String logString2 = LogEntryRenderer.renderLogEntry(reader.readNext(), reader);
		assertEquals("num: 63 str: second blurb", logString2);
		String logString3 = LogEntryRenderer.renderLogEntry(reader.readNext(), reader);
		assertEquals("Test raw log", logString3);
	}

	@Test
	public void stressTest() throws IOException, InterruptedException {
		final long NUM_THREADS = 8;
		final long RUN_TIME_SECONDS = 3;

		LogWriter writer = new LogWriter(new File(workingDir.getRoot(), "stress_test.log").getPath());
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for (int j = 0; j < NUM_THREADS; ++j) {
			Thread t = new Thread(() ->{
				long end = System.currentTimeMillis() + RUN_TIME_SECONDS * 1000;
				while (System.currentTimeMillis() < end) {
					writer.logStoredFormat(
						LogEntry.newBuilder()
							.setTime(Logger.getTime())
							.setSeverity(Severity.ERROR)
							.setCategory(Category.AUTONOMOUS)
							.setMessageStr("num: %d str: %s")
							.addArg(LogValue.newBuilder().setIntValue(42))
							.addArg(LogValue.newBuilder().setStringValue("my string")));
					writer.log(
						LogEntry.newBuilder()
							.setTime(Logger.getTime())
							.setSeverity(Severity.WARNING)
							.setCategory(Category.AUTONOMOUS)
							.setMessageStr("Test raw log")
							.build());
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