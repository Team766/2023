package com.team766.logging;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.team766.library.LossyPriorityQueue;

public class LogWriter {
	private static final int QUEUE_SIZE = 50;
	
	private static ArrayList<LogWriter> loggers = new ArrayList<LogWriter>();
	
	private static class LogUncaughtException implements Thread.UncaughtExceptionHandler {
		public void uncaughtException(Thread t, Throwable e) {
			e.printStackTrace();
			for (LogWriter log : loggers) {
				log.logRaw(Severity.ERROR, Category.JAVA_EXCEPTION, e.toString());
				log.close();
			}
		}
	}
	
	static {
		Thread.setDefaultUncaughtExceptionHandler(new LogUncaughtException());
	}
	
	private LossyPriorityQueue<LogEntry> m_entriesQueue;
	
	private static Thread m_workerThread;
	private boolean m_running = true;
	
	private HashMap<String, Integer> m_formatStringIndices = new HashMap<String, Integer>();
	
	private FileOutputStream m_fileStream;
	private ObjectOutputStream m_objectStream;
	
	private Severity m_minSeverity = Severity.INFO;
	
	public LogWriter(String filename) throws IOException {
		m_entriesQueue = new LossyPriorityQueue<LogEntry>(QUEUE_SIZE, new LogEntryComparator());
		m_fileStream = new FileOutputStream(filename);
		m_objectStream = new ObjectOutputStream(m_fileStream);
		m_workerThread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					LogEntry entry;
					try {
						entry = m_entriesQueue.poll();
					} catch (InterruptedException e) {
						System.out.println("Logger thread received interruption");
						continue;
					}
					if (entry instanceof CloseLogPseudoEntry) {
						return;
					}
					entry.write(m_objectStream);
				}
			}
		});
		m_workerThread.start();
	}

	public void close() {
		m_running = false;
		m_entriesQueue.add(new CloseLogPseudoEntry());
		try {
			m_entriesQueue.waitForEmpty();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			m_objectStream.flush();
			m_fileStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			m_workerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			m_objectStream.close();
			m_fileStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setSeverityFilter(Severity threshold) {
		m_minSeverity = threshold;
	}
	
	public void log(Severity severity, Category category, String format, Object... args) {
		if (severity.compareTo(m_minSeverity) < 0) {
			return;
		}
		if (!m_running) {
			System.out.println("Log message during shutdown: " + String.format(format, args));
			return;
		}
		Integer index = m_formatStringIndices.get(format);
		LogEntry logEntry;
		if (index == null) {
			m_formatStringIndices.put(format, m_formatStringIndices.size());
			if (m_formatStringIndices.size() % 100 == 0) {
				System.out.println("You're logging a lot of unique messages. Please switch to using logRaw()");
			}
			logEntry = new LogEntryWithFormat(severity, new Date(), category, format, args);
		} else {
			logEntry = new FormattedLogEntry(severity, new Date(), category, index, args);
		}
		m_entriesQueue.add(logEntry);
	}
	
	public void logRaw(Severity severity, Category category, String message) {
		if (severity.compareTo(m_minSeverity) < 0) {
			return;
		}
		if (!m_running) {
			System.out.println("Log message during shutdown: " + message);
			return;
		}
		m_entriesQueue.add(new RawLogEntry(severity, new Date(), category, message));
	}
}
