package com.team766.logging;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;

import com.team766.config.ConfigFileReader;
import com.team766.library.CircularBuffer;

public class Logger {
	private static class LogUncaughtException implements Thread.UncaughtExceptionHandler {
		public void uncaughtException(Thread t, Throwable e) {
			e.printStackTrace();

			LoggerExceptionUtils.logException(e);

			if (m_logWriter != null) {
				try {
					m_logWriter.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			System.exit(1);
		}
	}

	private static final int MAX_NUM_RECENT_ENTRIES = 100;
	
	private static EnumMap<Category, Logger> m_loggers = new EnumMap<Category, Logger>(Category.class);
	private static LogWriter m_logWriter = null;
	private CircularBuffer<LogEntry> m_recentEntries = new CircularBuffer<LogEntry>(MAX_NUM_RECENT_ENTRIES);
	private static Object s_lastWriteTimeSync = new Object();
	private static long s_lastWriteTime = 0L;

	public static String logFilePathBase = null;
	
	static {
		for (Category category : Category.values()) {
			m_loggers.put(category, new Logger(category));
		}
		try {
			ConfigFileReader config_file = ConfigFileReader.getInstance();
			if (config_file != null) {
				logFilePathBase = config_file.getString("logFilePath").get();
				new File(logFilePathBase).mkdirs();
				final String timestamp = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
				final String logFilePath = new File(logFilePathBase, timestamp).getAbsolutePath();
				m_logWriter = new LogWriter(logFilePath);
				get(Category.CONFIGURATION).logRaw(Severity.INFO, "Logging to " + logFilePath);
			} else {
				get(Category.CONFIGURATION).logRaw(Severity.ERROR, "Config file is not available. Logs will only be in-memory and will be lost when the robot is turned off.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LoggerExceptionUtils.logException(e);
		}

		Thread.setDefaultUncaughtExceptionHandler(new LogUncaughtException());
	}
	
	public static Logger get(Category category) {
		return m_loggers.get(category);
	}

	static long getTime() {
		long nowNanosec = new Date().getTime() * 1000000;
		synchronized(s_lastWriteTimeSync) {
			// Ensure that log entries' timestamps are unique. This is important
			// because the log viewer uses an entry's timestamp as a unique ID,
			// and we don't want two different log entries to accidentally
			// compare as equal.
			nowNanosec = s_lastWriteTime = Math.max(nowNanosec, s_lastWriteTime);
		}
		return nowNanosec;
	}
	
	private final Category m_category;
	
	private Logger(Category category) {
		m_category = category;
	}
	
	public Collection<LogEntry> recentEntries() {
		return Collections.unmodifiableCollection(m_recentEntries);
	}
	
	public void logData(Severity severity, String format, Object... args) {
		var entry = LogEntry.newBuilder()
				.setTime(getTime())
				.setSeverity(severity)
				.setCategory(m_category);
		{
			entry.setMessageStr(String.format(format, args));
			m_recentEntries.add(entry.build());
		}
		entry.setMessageStr(format);
		for (Object arg : args) {
			SerializationUtils.valueToProto(arg, entry.addArgBuilder());
		}
		if (m_logWriter != null) {
			m_logWriter.logStoredFormat(entry);
		}
	}
	
	public void logRaw(Severity severity, String message) {
		var entry = LogEntry.newBuilder()
				.setTime(getTime())
				.setSeverity(severity)
				.setCategory(m_category)
				.setMessageStr(message)
				.build();
		m_recentEntries.add(entry);
		if (m_logWriter != null) {
			m_logWriter.log(entry);
		}
	}

	void logOnlyInMemory(Severity severity, String message) {
		var entry = LogEntry.newBuilder()
				.setTime(getTime())
				.setSeverity(severity)
				.setCategory(m_category)
				.setMessageStr(message)
				.build();
		m_recentEntries.add(entry);
	}
}
