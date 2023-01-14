package com.team766.logging;

import java.util.Arrays;
import java.util.Comparator;

class LogEntryComparator implements Comparator<LogEntry> {
	// This LogEntry should be ordered after any normal LogEntries.
	// It is used to signal to the log writing thread that it should exit.
	// We want it to come last in the priority queue so that any pending log
	// entries get written before the thread terminates.
	public static final LogEntry TERMINATION_SENTINAL =
		LogEntry.newBuilder()
			.setSeverity(Arrays.stream(Severity.values()).min(Comparator.naturalOrder()).get())
			.setTime(Long.MAX_VALUE)
			.build();

	@Override
	public int compare(LogEntry o1, LogEntry o2) {
		// Sort by highest severity first
		int severityResult = -o1.getSeverity().compareTo(o2.getSeverity());
		if (severityResult != 0) {
			return severityResult;
		}
		// Then sort by earliest time.
		// Each Category's logger ensures these are unique. This is important
		// because we don't want two different log entries to accidentally
		// compare as equal.
		return Long.compare(o1.getTime(), o2.getTime());
	}
}