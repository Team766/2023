package com.team766.web;

import java.util.Arrays;
import java.util.Map;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.RawLogEntry;

public class LogViewer implements WebServer.Handler {
	@Override
	public String handle(Map<String, Object> params) {
		Category category = Category.JAVA_EXCEPTION;
		String categoryName = (String)params.get("category");
		if (categoryName != null) {
			category = Enum.valueOf(Category.class, categoryName);
		}
		
		Logger logger = Logger.get(category);
		
		String r = "<h1>Log: " + category.toString() + "</h1>";
		r += "<form><p>";
		r += HtmlElements.buildDropDown("category", category.name(), Arrays.stream(Category.values()).map(Category::name).toArray(String[]::new));
		r += "<input type=\"submit\" value=\"View\">";
		r += "</p></form>\n";
		r += "<table border=\"1\">\n";
		for (RawLogEntry entry : logger.recentEntries()) {
			r += String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td style=\"white-space: pre\">%s</td></tr>", entry.getCategory(), entry.getTime(), entry.getSeverity(), entry.format());
		}
		r += "</table>\n";
		
		return r;
	}

	@Override
	public String title() {
		return "Log Viewer";
	}
}
