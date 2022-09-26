package com.team766.logging;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;

public class LogReader {

	private FileInputStream m_fileStream;
	private CodedInputStream m_dataStream;
	private LogEntry.Builder m_entryBuilder;
	private ArrayList<String> m_formatStrings;

	public LogReader(String filename) throws IOException {
		m_fileStream = new FileInputStream(filename);
		m_dataStream = CodedInputStream.newInstance(m_fileStream);
		m_entryBuilder = LogEntry.newBuilder();
		m_formatStrings = new ArrayList<String>();
	}
	
	public LogEntry readNext() throws IOException {
		m_entryBuilder.clear();
		m_dataStream.readMessage(m_entryBuilder, ExtensionRegistryLite.getEmptyRegistry());
		LogEntry entry = m_entryBuilder.build();
		if (entry.hasMessageIndex() && entry.hasMessageStr()) {
			final int index = entry.getMessageIndex();
			final String format = entry.getMessageStr();
			m_formatStrings.ensureCapacity(index + 1);
			while (m_formatStrings.size() <= index) {
				m_formatStrings.add(null);
			}
			m_formatStrings.set(index, format);
		}
		return entry;
	}
	
	String getFormatString(int index) {
		String str;
		try {
			str = m_formatStrings.get(index);
		} catch (IndexOutOfBoundsException ex) {
			throw new IllegalArgumentException("Unknown format string index: " + index);
		}
		if (str == null) {
			throw new IllegalArgumentException("Unknown format string index: " + index);
		}
		return str;
	}
}
