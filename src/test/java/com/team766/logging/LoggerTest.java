package com.team766.logging;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.team766.logging.Category;
import com.team766.logging.LogReader;
import com.team766.logging.LogWriter;
import com.team766.logging.Severity;

public class LoggerTest {
	@Test
	public void test() throws IOException {
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
}