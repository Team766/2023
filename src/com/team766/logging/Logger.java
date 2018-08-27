package com.team766.logging;

import java.io.IOException;
import java.util.EnumMap;

import com.team766.config.ConfigFileReader;

public class Logger {
	private static EnumMap<Category, Logger> m_loggers = new EnumMap<Category, Logger>(Category.class);
	private static LogWriter m_logWriter;
	
	static {
		try {
			m_logWriter = new LogWriter(ConfigFileReader.getInstance().getString("logFilePath"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Category category : Category.values()) {
			m_loggers.put(category, new Logger(category));
		}
	}
	
	public static Logger get(Category category) {
		return m_loggers.get(category);
	}
	
	private final Category m_category;
	
	private Logger(Category category) {
		m_category = category;
	}
	
	public void log(Severity severity, String format, Object... args) {
		m_logWriter.log(severity, m_category, format, args);
	}
	
	public void logRaw(Severity severity, String message) {
		m_logWriter.logRaw(severity, m_category, message);
	}
}
