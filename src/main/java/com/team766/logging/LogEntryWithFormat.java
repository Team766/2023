package com.team766.logging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

public class LogEntryWithFormat implements LogEntry {
	public static LogEntryWithFormat deserialize(ObjectInputStream objectStream) throws IOException {
		Severity severity = Severity.fromInteger(objectStream.readByte());
		Category category = Category.fromInteger(objectStream.readByte());
		Date time = new Date(objectStream.readLong());
		String format;
		Object[] args;
		try {
			format = (String) objectStream.readObject();
			args = (Object[]) objectStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return new LogEntryWithFormat(severity, time, category, format, args);
	}
	
	private final Severity m_severity;
	private final Date m_time;
	private final Category m_category;
	private final String m_format;
	private final Object[] m_args;
	
	public LogEntryWithFormat(Severity severity, Date time, Category category, String format, Object[] args) {
		m_severity = severity;
		m_time = time;
		m_category = category;
		m_format = format;
		m_args = args;
	}
	
	@Override
	public void write(ObjectOutputStream objectStream) {
		try {
			objectStream.writeByte(StreamTags.LOG_ENTRY_WITH_FORMAT.ordinal());
			objectStream.writeByte(m_severity.ordinal());
			objectStream.writeByte(m_category.ordinal());
			objectStream.writeLong(m_time.getTime());
			objectStream.writeObject(m_format);
			objectStream.writeObject(m_args);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getFormat() {
		return m_format;
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
		return String.format(m_format, m_args);
	}
}
