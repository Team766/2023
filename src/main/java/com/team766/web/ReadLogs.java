package com.team766.web;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LogEntry;
import com.team766.logging.LogEntryRenderer;
import com.team766.logging.LogReader;
import com.team766.logging.Severity;

public class ReadLogs implements WebServer.Handler {
	private static final String ENDPOINT = "/readlogs";
	private static final String ALL_ERRORS_NAME = "All Errors";
	private static final int ENTRIES_PER_PAGE = 100;

	private HashMap<String, LogReader> logReaders = new HashMap<String, LogReader>();
	private HashMap<String, String> readerDescriptions = new HashMap<String, String>();
	private HashMap<String, Iterator<LogEntry>> readerStreams = new HashMap<String, Iterator<LogEntry>>();

	private static String makeLogEntriesTable(LogReader reader, Iterator<LogEntry> entries) {
		String r = "<table id=\"log-entries\" border=\"1\">\n";
		for (int i = 0; i < ENTRIES_PER_PAGE; ++i) {
			if (!entries.hasNext()) {
				break;
			}
			LogEntry entry = entries.next();
			r += String.format(
				"<tr><td style=\"white-space: pre\">%s</td><td style=\"white-space: pre\">%s</td><td style=\"white-space: pre\">%s</td><td style=\"white-space: pre\">%s</td></tr>\n",
				entry.getCategory(), entry.getTime(), entry.getSeverity(), LogEntryRenderer.renderLogEntry(entry, reader));
		}
		r += "</table>";
		return r;
	}

	private String makePage(String id) {
		String r = String.join("\n", new String[]{
			"<p>Free disk space: "
				+ NumberFormat.getNumberInstance(Locale.US).format(
					new File(Logger.logFilePathBase).getUsableSpace())
				+ "</p>",
			"<form action=\"" + ENDPOINT + "\"><p>",
			HtmlElements.buildDropDown(
				"logFile",
				"",
				Logger.logFilePathBase == null
					? new String[0]
					: new File(Logger.logFilePathBase).list()),
			HtmlElements.buildDropDown(
				"category",
				"",
				Stream.concat(
					Stream.of("", ALL_ERRORS_NAME),
					Arrays.stream(Category.values()).map(Category::name)
					).toArray(String[]::new)),
			"<input type=\"submit\" value=\"Open Log\">",
			"</p></form>",
		});
		if (id != null) {
			r += String.join("\n", new String[]{
				"<h1>Log: " + readerDescriptions.get(id) + "</h1>",
				makeLogEntriesTable(logReaders.get(id), readerStreams.get(id)),
				"<input type=\"button\" onclick=\"window.location = '" + ENDPOINT + "?id=" + id + "'; this.disabled=true; this.value='Loading...';\" value=\"Next page\" />",
			});
		}
		return r;
	}

	private String makeReader(
		String logFile,
		String description,
		Function<Stream<LogEntry>, Stream<LogEntry>> filter
	) {
		LogReader reader;
		try {
			reader = new LogReader(new File(Logger.logFilePathBase, logFile).getAbsolutePath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String id = Integer.toString(logReaders.size());
		logReaders.put(id, reader);
		readerDescriptions.put(id, logFile + " " + description);
		readerStreams.put(id, filter.apply(Stream.generate(() -> {
			try {
				return reader.readNext();
			} catch (EOFException e) {
				return null;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).takeWhile(e -> e != null)).iterator());
		return id;
	}

	private String makeUnfilteredReader(String logFile) {
		return makeReader(
			logFile,
			"",
			s -> s
		);
	}

	private String makeCategoryReader(String logFile, Category category) {
		return makeReader(
			logFile,
			category.name(),
			s -> s.filter(e -> e.getCategory() == category)
		);
	}

	private String makeAllErrorsReader(String logFile) {
		return makeReader(
			logFile,
			ALL_ERRORS_NAME,
			s -> s.filter(entry -> entry.getSeverity() == Severity.ERROR)
		);
	}

	@Override
	public String endpoint() {
		return ENDPOINT;
	}

	@Override
	public String handle(Map<String, Object> params) {
		String id = (String)params.get("id");
		String logFile = (String)params.get("logFile");
		String categoryName = (String)params.get("category");
		if (!logReaders.containsKey(id)) {
			id = null;
		}
		if (logFile != null) {
			if (categoryName == null || categoryName.equals("")) {
				id = makeUnfilteredReader(logFile);
			} else if (categoryName.equals(ALL_ERRORS_NAME)) {
				id = makeAllErrorsReader(logFile);
			} else {
				Category category = Enum.valueOf(Category.class, categoryName);
				id = makeCategoryReader(logFile, category);
			}
		}
		return makePage(id);
	}

	@Override
	public String title() {
		return "Log Reader";
	}
}
