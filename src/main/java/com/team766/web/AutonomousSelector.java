package com.team766.web;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.team766.framework.AutonomousMode;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public class AutonomousSelector implements WebServer.Handler {
	private static final String ENDPOINT = "/auton";

	private final AutonomousMode[] m_autonModes;
	private final String[] m_autonModeNames;
	private AutonomousMode m_selectedAutonMode;
	
	public AutonomousSelector(AutonomousMode[] autonModes) {
		m_autonModes = autonModes;
		m_autonModeNames =
			Arrays.stream(autonModes).map(m -> m.name()).toArray(String[]::new);
		if (m_autonModeNames.length > 0) {
			m_selectedAutonMode = m_autonModes[0];
		} else {
			Logger.get(Category.AUTONOMOUS).logRaw(
				Severity.WARNING,
				"No autonomous modes were declared in AutonomousModes.java");
			m_selectedAutonMode = null;
		}
	}
	
	public AutonomousMode getSelectedAutonMode() {
		return m_selectedAutonMode;
	}

	@Override
	public String endpoint() {
		return ENDPOINT;
	}
	
	@Override
	public String handle(Map<String, Object> params) {
		String locationReplaceScript = "";
		{
			final String selectedAutonModeName = (String)params.get("AutoMode");
			if (selectedAutonModeName != null) {
				final Optional<AutonomousMode> selectedAutonMode =
					Arrays.stream(m_autonModes).filter(m -> m.name().equals(selectedAutonModeName)).findFirst();
				if (selectedAutonMode.isEmpty()) {
					Logger.get(Category.AUTONOMOUS).logData(
						Severity.ERROR,
						"Internal framework error: Inconsistent name for selected autonomous mode (selected: %s ; available: %s). Autonomous mode will not run.",
						selectedAutonModeName,
						Arrays.stream(m_autonModes).map(m -> m.name()).collect(Collectors.joining(",")));
				} else {
					m_selectedAutonMode = selectedAutonMode.get().clone();
				}

				locationReplaceScript = "<script>window.location.replace(window.location.pathname);</script>";
			}
		}
		
		final String selectedAutonModeName = m_selectedAutonMode != null ? m_selectedAutonMode.name() : "<none>";
		return String.join("\n", new String[]{
			locationReplaceScript,
			"<h1>Autonomous Mode Selector</h1>",
			"<h3 id=\"current-mode\">Current Mode: " + selectedAutonModeName + "</h1>",
			"<form>",
			"<p>" + HtmlElements.buildDropDown("AutoMode", selectedAutonModeName, m_autonModeNames) + "</p>",
			"<input type=\"submit\" value=\"Submit\"></form>",
			"<script>",
			"  function refreshAutoMode() {",
			"    var xhttp = new XMLHttpRequest();",
			"    xhttp.onreadystatechange = function() {",
			"      if (this.readyState == 4 && this.status == 200) {",
			"        var newDoc = new DOMParser().parseFromString(this.responseText, 'text/html')",
			"        var oldMode = document.getElementById('current-mode');",
			"        oldMode.parentNode.replaceChild(",
			"            document.importNode(newDoc.querySelector('#current-mode'), true),",
			"            oldMode);",
			"     }",
			"    };",
			"    xhttp.open('GET', \"" + ENDPOINT + "\", true);",
			"    xhttp.send();",
			"  }",
			"  setInterval(refreshAutoMode, 1000);",
			"</script>",
		});
	}

	@Override
	public String title() {
		return "Autonomous Selector";
	}
}
