package com.team766.logging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public abstract class LogEntry {
	private static AtomicLong s_objectIdCounter = new AtomicLong();

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

	private final long m_objectId = s_objectIdCounter.getAndIncrement();

	public abstract void write(ObjectOutputStream objectStream);

	public abstract Severity getSeverity();

	public abstract Category getCategory();

	public abstract Date getTime();

	public abstract String format(LogReader reader);

	/**
	 * Should return a number which is unique to this in-memory object.
	 * This value is not serialized, so the objectId of a LogEntry read from a
	 * log file may be different than its objectId when it was written.
	 */
	public final long objectId() {
		return m_objectId;
	}
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