package com.team766.logging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

public class RawLogEntry implements LogEntry {
	public static RawLogEntry deserialize(ObjectInputStream objectStream) throws IOException {
		Severity severity = Severity.fromInteger(objectStream.readByte());
		Category category = Category.fromInteger(objectStream.readByte());
		Date time = new Date(objectStream.readLong());
		String message;
		try {
			message = (String) objectStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return new RawLogEntry(severity, time, category, message);
	}
	
	private final Severity m_severity;
	private final Date m_time;
	private final Category m_category;
	private final String m_message;
	
	public RawLogEntry(Severity severity, Date time, Category category, String message) {
		m_severity = severity;
		m_time = time;
		m_category = category;
		m_message = message;
	}
	
	@Override
	public void write(ObjectOutputStream objectStream) {
		try {
			objectStream.writeByte(StreamTags.RAW_LOG_ENTRY.ordinal());
			objectStream.writeByte(m_severity.ordinal());
			objectStream.writeByte(m_category.ordinal());
			objectStream.writeLong(m_time.getTime());
			objectStream.writeObject(m_message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Severity getSeverity() {
		return m_severity;
	}

	@Override
	public Date getTime() {
		return m_time;
	}
	
	@Override
	public Category getCategory() {
		return m_category;
	}
	
	@Override
	public String format(LogReader reader) {
		return m_message;
	}
	
	public String format() {
		return m_message;
	}
}
