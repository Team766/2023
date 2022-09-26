package com.team766.web;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LogEntry;
import com.team766.logging.LogEntryRenderer;
import com.team766.logging.Severity;

public class LogViewer implements WebServer.Handler {
	private static final String ENDPOINT = "/logs";
	private static final String ALL_ERRORS_NAME = "All Errors";

	private static String makeLogEntriesTable(Iterable<LogEntry> entries) {
		String r = "<table id=\"log-entries\" border=\"1\">\n";
		for (LogEntry entry : entries) {
			r += String.format(
				"<tr><td style=\"white-space: pre\">%s</td><td style=\"white-space: pre\">%s</td><td style=\"white-space: pre\">%s</td><td style=\"white-space: pre\">%s</td></tr>\n",
				entry.getCategory(), entry.getTime(), entry.getSeverity(), LogEntryRenderer.renderLogEntry(entry, null));
		}
		r += "</table>";
		return r;
	}

	private static String makePage(String categoryName, Iterable<LogEntry> entries) {
		return String.join("\n", new String[]{
			"<h1>Log: " + categoryName + "</h1>",
			"<form action=\"" + ENDPOINT + "\"><p>",
			HtmlElements.buildDropDown(
				"category",
				categoryName,
				Stream.concat(
					Stream.of(ALL_ERRORS_NAME),
					Arrays.stream(Category.values()).map(Category::name)
				).toArray(String[]::new)),
			"<input type=\"submit\" value=\"View\">",
			"</p></form>",
			makeLogEntriesTable(entries),
			"<input type=\"button\" onclick=\"refreshLog();\" value=\"Refresh\" />",
			"<input type=\"checkbox\" id=\"refresh-enabled\" checked=\"checked\" />",
			"<label for=\"refresh-enabled\">Enable automatic refresh</label>",
			"<script>",
			"  function afterLoad(event) {",
			//"    window.scrollBy(0, document.body.scrollHeight);",
			//"    setTimeout(function(){",
			//"      window.scrollBy(0, document.body.scrollHeight);",
			//"    }, 150);",
			"  }",
			"  document.addEventListener('DOMContentLoaded', afterLoad);",
			"  function refreshLog() {",
			"    var xhttp = new XMLHttpRequest();",
			"    xhttp.onreadystatechange = function() {",
			"      if (this.readyState == 4 && this.status == 200) {",
			"        var newDoc = new DOMParser().parseFromString(this.responseText, 'text/html')",
			"        var oldTable = document.getElementById('log-entries');",
			"        oldTable.parentNode.replaceChild(",
			"            document.importNode(newDoc.querySelector('#log-entries'), true),",
			"            oldTable);",
			"        afterLoad();",
			"     }",
			"    };",
			"    xhttp.open('GET', window.location.href, true);",
			"    xhttp.send();",
			"  }",
			"  setInterval(function(){",
			"    if (document.getElementById('refresh-enabled').checked) {",
			"      refreshLog();",
			"    }",
			"  }, 1000);",
			"</script>",
		});
	}

	static String makeAllErrorsPage() {
		return makePage(
			ALL_ERRORS_NAME,
			Arrays.stream(Category.values())
				.flatMap(category -> Logger.get(category).recentEntries().stream())
				.filter(entry -> entry.getSeverity() == Severity.ERROR)
				::iterator);
	}

	@Override
	public String endpoint() {
		return ENDPOINT;
	}

	@Override
	public String handle(Map<String, Object> params) {
		String categoryName = (String)params.get("category");
		if (categoryName == null || categoryName.equals(ALL_ERRORS_NAME)) {
			return makeAllErrorsPage();
		} else {
			Category category = Enum.valueOf(Category.class, categoryName);
			return makePage(category.name(), Logger.get(category).recentEntries());
		}
	}

	@Override
	public String title() {
		return "Log Viewer";
	}
}
