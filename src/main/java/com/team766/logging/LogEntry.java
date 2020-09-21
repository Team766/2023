package com.team766.logging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

public interface LogEntry {
	public static LogEntry deserialize(ObjectInputStream objectStream) throws IOException {
		StreamTags tag = StreamTags.fromInteger(objectStream.readByte());
		switch(tag) {
		case FORMATTED_LOG_ENTRY:
			return FormattedLogEntry.deserialize(objectStream);
		case LOG_ENTRY_WITH_FORMAT:
			return LogEntryWithFormat.deserialize(objectStream);
		case RAW_LOG_ENTRY:
			return RawLogEntry.deserialize(objectStream);
		default:
			throw new RuntimeException("Unknown stream tag");
		}
	}
	
	public void write(ObjectOutputStream objectStream);
	
	public Severity getSeverity();
	
	public Category getCategory();
	
	public Date getTime();
	
	public String format(LogReader reader);
}

enum StreamTags {
	LOG_ENTRY_WITH_FORMAT,
	// byte tag = LOG_ENTRY_WITH_FORMAT
	// byte severity
	// byte category
	// long time_in_millis
	// String format
	// Object... args
	
	FORMATTED_LOG_ENTRY,
	// byte tag = FORMATTED_LOG_ENTRY
	// byte severity
	// byte category
	// long time_in_millis
	// int formatStringIndex
	// Object... args
	
	RAW_LOG_ENTRY;
	// byte tag = RAW_LOG_ENTRY
	// byte severity
	// byte category
	// long time_in_millis
	// String message
	
	private static final StreamTags[] VALUES = StreamTags.values();
	public static StreamTags fromInteger(byte x) {
		return VALUES[x];
	}
}