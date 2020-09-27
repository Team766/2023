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
		
		String r = String.join("\n", new String[]{
			"<h1>Log: " + category.toString() + "</h1>",
			"<form><p>",
			HtmlElements.buildDropDown("category", category.name(), Arrays.stream(Category.values()).map(Category::name).toArray(String[]::new)),
			"<input type=\"submit\" value=\"View\">",
			"</p></form>",
			"<table id=\"log-entries\" border=\"1\">"
		});
		for (RawLogEntry entry : logger.recentEntries()) {
			r += String.format(
				"<tr><td style=\"white-space: pre\">%s</td><td style=\"white-space: pre\">%s</td><td style=\"white-space: pre\">%s</td><td style=\"white-space: pre\">%s</td></tr>\n",
				entry.getCategory(), entry.getTime(), entry.getSeverity(), entry.format());
		}
		r += String.join("\n", new String[]{
			"</table>",
			"<input type=\"button\" onclick=\"window.location.reload();\" value=\"Refresh\" />",
			"<input type=\"checkbox\" id=\"refresh-enabled\" checked=\"checked\" />",
			"<label for=\"refresh-enabled\">Enable automatic refresh</label>",
			"<script>",
			"  function afterLoad(event) {",
			"    window.scrollBy(0, document.body.scrollHeight);",
			"    setTimeout(function(){",
			"      window.scrollBy(0, document.body.scrollHeight);",
			"    }, 150);",
			"  }",
			"  document.addEventListener('DOMContentLoaded', afterLoad);",
			"  function refresh() {",
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
			"      refresh();",
			"    }",
			"  }, 1000);",
			"</script>",
		});
		
		return r;
	}

	@Override
	public String title() {
		return "Log Viewer";
	}
}
