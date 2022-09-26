package com.team766.web;

import java.util.Map;
import com.team766.web.dashboard.Widget;

public class Dashboard implements WebServer.Handler {
	private static final String ENDPOINT = "/dashboard";

	static String makeDashboardPage() {
		String page = "<h1>Dashboard Widgets</h1>\n";
		page += "<div id=\"dashboard\">\n";
		for (var widget : Widget.listWidgets()) {
			page += widget.render();
			page += '\n';
		}
		page += "</div>\n";
		page += String.join("\n", new String[]{
			"<script>",
			"  function refreshDashboard() {",
			"    var xhttp = new XMLHttpRequest();",
			"    xhttp.onreadystatechange = function() {",
			"      if (this.readyState == 4 && this.status == 200) {",
			"        var newDoc = new DOMParser().parseFromString(this.responseText, 'text/html')",
			"        var oldTable = document.getElementById('dashboard');",
			"        oldTable.parentNode.replaceChild(",
			"            document.importNode(newDoc.querySelector('#dashboard'), true),",
			"            oldTable);",
			"        setTimeout(refreshDashboard, 300);",
			"     }",
			"    };",
			"    xhttp.open('GET', \"" + ENDPOINT + "\", true);",
			"    xhttp.send();",
			"  }",
			"  refreshDashboard();",
			"  setInterval(refreshDashboard, 5000);",
			"</script>",
		});
		return page;
	}

	@Override
	public String endpoint() {
		return ENDPOINT;
	}

	@Override
	public String title() {
		return "Dashboard";
	}

	@Override
	public String handle(Map<String, Object> params) {
		return makeDashboardPage();
	}

	@Override
	public boolean showInMenu() {
		return false;
	}
}
