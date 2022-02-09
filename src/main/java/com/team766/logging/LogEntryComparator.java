package com.team766.logging;

import java.util.Comparator;

class LogEntryComparator implements Comparator<LogEntry> {
	@Override
	public int compare(LogEntry o1, LogEntry o2) {
		// Sort by highest severity first
		int severityResult = -o1.getSeverity().compareTo(o2.getSeverity());
		if (severityResult != 0) {
			return severityResult;
		}
		// Then sort by earliest time
		int timeResult = o1.getTime().compareTo(o2.getTime());
		if (timeResult != 0) {
			return timeResult;
		}
		// Else compare objectIds. This ensures that we only return 0 from
		// this comparison if o1 and o2 are actually the same log entry.
		return Long.compare(o1.objectId(), o2.objectId());
	}
}