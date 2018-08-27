package com.team766.logging;

import java.util.Comparator;

class LogEntryComparator implements Comparator<LogEntry> {
	@Override
	public int compare(LogEntry o1, LogEntry o2) {
		int severityResult = o1.getSeverity().compareTo(o2.getSeverity());
		if (severityResult != 0) {
			// Sort by highest severity first
			return -severityResult;
		}
		// Then sort by earliest time
		return o1.getTime().compareTo(o2.getTime());
	}
}