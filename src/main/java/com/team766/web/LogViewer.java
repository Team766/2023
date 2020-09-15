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
		
        String r = "<h1>Log: " + category.toString() + "</h1>\n";
		r += "<form><p>";
		r += HtmlElements.buildDropDown("category", category.name(), Arrays.stream(Category.values()).map(Category::name).toArray(String[]::new));
		r += "<input type=\"submit\" value=\"View\">";
		r += "</p></form>\n";
        r += "<table border=\"1\">\n";
		for (RawLogEntry entry : logger.recentEntries()) {
			r += String.format(
                "<tr><td style=\"white-space: pre\">%s</td><td style=\"white-space: pre\">%s</td><td style=\"white-space: pre\">%s</td><td style=\"white-space: pre\">%s</td></tr>\n",
                entry.getCategory(), entry.getTime(), entry.getSeverity(), entry.format());
		}
        r += "</table>\n";
        r += "<input type=\"button\" onclick=\"window.location.reload();\" value=\"Refresh\" />\n";
        r += "<input type=\"checkbox\" id=\"refresh-enabled\" checked=\"checked\" />";
        r += "<label for=\"refresh-enabled\">Enable automatic refresh</label>\n";
        r += "<script>";
        r += "  setInterval(function(){";
        r += "    if (document.getElementById('refresh-enabled').checked) {";
        r += "      location.reload();";
        r += "    }";
        r += "  }, 3000);";
        r += "  document.addEventListener('DOMContentLoaded', function(event) {";
        r += "    window.scrollBy(0, document.body.scrollHeight);";
        r += "    setTimeout(function(){";
        r += "      window.scrollBy(0, document.body.scrollHeight);";
        r += "    }, 150);";
        r += "  });";
        r += "</script>";
		
		return r;
	}

	@Override
	public String title() {
		return "Log Viewer";
	}
}
