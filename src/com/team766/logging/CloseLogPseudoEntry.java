package com.team766.logging;

import java.io.ObjectOutputStream;
import java.util.Date;

public class CloseLogPseudoEntry implements LogEntry {
	@Override
	public void write(ObjectOutputStream objectStream) {
		throw new UnsupportedOperationException("CloseLogPseudoEntry should not be written");
	}

	@Override
	public Severity getSeverity() {
		return Severity.MIN_SEVERITY;
	}

	@Override
	public Date getTime() {
		return new Date(Long.MAX_VALUE);
	}
	
	@Override
	public Category getCategory() {
		throw new UnsupportedOperationException("CloseLogPseudoEntry should not be read");
	}

	@Override
	public String format(LogReader reader) {
		throw new UnsupportedOperationException("CloseLogPseudoEntry should not be read");
	}
}
