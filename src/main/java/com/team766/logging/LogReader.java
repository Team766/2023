package com.team766.logging;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class LogReader {

	private FileInputStream m_fileStream;
	private ObjectInputStream m_objectStream;
	private ArrayList<String> m_formatStrings;

	public LogReader(String filename) throws IOException {
		m_fileStream = new FileInputStream(filename);
		m_objectStream = new ObjectInputStream(m_fileStream);
		m_formatStrings = new ArrayList<String>();
	}
	
	public LogEntry readNext() throws IOException {
		LogEntry entry = LogEntry.deserialize(m_objectStream);
		if (entry instanceof LogEntryWithFormat) {
			m_formatStrings.add(((LogEntryWithFormat)entry).getFormat());
		}
		return entry;
	}
	
	String getFormatString(int index) {
		return m_formatStrings.get(index);
	}
}
