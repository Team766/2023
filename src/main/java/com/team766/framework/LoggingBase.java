package com.team766.framework;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public abstract class LoggingBase {
	protected Category loggerCategory = Category.PROCEDURES;

	public abstract String getName();

	protected void log(String message) {
		log(Severity.INFO, message);
	}

	protected void log(Severity severity, String message) {
		Logger.get(loggerCategory).logRaw(severity, getName() + ": " + message);
	}
	
	protected void log(String format, Object... args) {
		log(Severity.INFO, format, args);
	}

	protected void log(Severity severity, String format, Object... args) {
		Logger.get(loggerCategory).logData(severity, getName() + ": " + format, args);
	}
}