package com.team766.logging;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;

import com.team766.config.ConfigFileReader;
import com.team766.library.CircularBuffer;

public class Logger {
	private static final int MAX_NUM_RECENT_ENTRIES = 100;
	
	private static EnumMap<Category, Logger> m_loggers = new EnumMap<Category, Logger>(Category.class);
	private static LogWriter m_logWriter = null;
	private CircularBuffer<RawLogEntry> m_recentEntries = new CircularBuffer<RawLogEntry>(MAX_NUM_RECENT_ENTRIES);
	
	static {
		for (Category category : Category.values()) {
			m_loggers.put(category, new Logger(category));
		}
		try {
			String logFilePath = ConfigFileReader.getInstance().getString("logFilePath").get();
			logFilePath = new File(logFilePath).getAbsolutePath();
			m_logWriter = new LogWriter(logFilePath);
			get(Category.CONFIGURATION).logRaw(Severity.INFO, "Logging to " + logFilePath);
		} catch (Exception e) {
			e.printStackTrace();
			LoggerExceptionUtils.logException(e);
		}
	}
	
	public static Logger get(Category category) {
		return m_loggers.get(category);
	}
	
	private final Category m_category;
	
	private Logger(Category category) {
		m_category = category;
	}
	
	public Collection<RawLogEntry> recentEntries() {
		return Collections.unmodifiableCollection(m_recentEntries);
	}
	
	public void logData(Severity severity, String format, Object... args) {
		m_recentEntries.add(new RawLogEntry(severity, new Date(), m_category, String.format(format, args)));
		if (m_logWriter != null) {
			m_logWriter.log(severity, m_category, format, args);
		}
	}
	
	public void logRaw(Severity severity, String message) {
		m_recentEntries.add(new RawLogEntry(severity, new Date(), m_category, message));
		if (m_logWriter != null) {
			m_logWriter.logRaw(severity, m_category, message);
		}
	}
}
