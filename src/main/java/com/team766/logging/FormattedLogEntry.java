package com.team766.logging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

public class FormattedLogEntry implements LogEntry {
	public static FormattedLogEntry deserialize(ObjectInputStream objectStream) throws IOException {
		Severity severity = Severity.fromInteger(objectStream.readByte());
		Category category = Category.fromInteger(objectStream.readByte());
		Date time = new Date(objectStream.readLong());
		int index = objectStream.readInt();
		Object[] args;
		try {
			args = (Object[]) objectStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return new FormattedLogEntry(severity, time, category, index, args);
	}
	
	private final Severity m_severity;
	private final Date m_time;
	private final Category m_category;
	private final int m_index;
	private final Object[] m_args;
	
	public FormattedLogEntry(Severity severity, Date time, Category category, int index, Object[] args) {
		m_severity = severity;
		m_time = time;
		m_category = category;
		m_index = index;
		m_args = args;
	}
	
	@Override
	public void write(ObjectOutputStream objectStream) {
		try {
			objectStream.writeByte(StreamTags.FORMATTED_LOG_ENTRY.ordinal());
			objectStream.writeByte(m_severity.ordinal());
			objectStream.writeByte(m_category.ordinal());
			objectStream.writeLong(m_time.getTime());
			objectStream.writeInt(m_index);
			objectStream.writeObject(m_args);
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
		return String.format(reader.getFormatString(m_index), m_args);
	}
}
