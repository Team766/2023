package com.team766.web;

import java.util.Arrays;
import java.util.Map;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;

public class AutonomousSelector implements WebServer.Handler {
	private String[] AUTONS;
	private String autonMode;
	
	public AutonomousSelector(String[] autons){
		AUTONS = autons;
		autonMode = AUTONS[0];
	}
	
	public AutonomousSelector(Class<? extends Enum<?>> auton_enum) {
		Enum<? extends Enum<?>>[] states = auton_enum.getEnumConstants();
		AUTONS = new String[states.length];
		for (int i = 0; i < states.length; ++i) {
			AUTONS[i] = states[i].toString();
		}
		if (AUTONS.length > 0) {
			autonMode = AUTONS[0];
		} else {
			Logger.get(Category.AUTONOMOUS).logRaw(Severity.WARNING, "No autonomous modes were declared in AutonomousModes.java");
			autonMode = null;
		}
	}
	
	public String getSelectedAutonModeString() {
		return autonMode;
	}
	
	public <E extends Enum<E>> E getSelectedAutonMode(Class<E> clazz) {
		try {
			return Enum.valueOf(clazz, autonMode);
		} catch (IllegalArgumentException | NullPointerException ex) {
			ex.printStackTrace();
			LoggerExceptionUtils.logException(ex);
			return null;
		}
	}

	@Override
	public String endpoint() {
		return "/values";
	}
	
	@Override
	public String handle(Map<String, Object> params) {
		String selectedAuto = (String)params.get("AutoMode");
		if (selectedAuto != null) {
			if (Arrays.stream(AUTONS).anyMatch(selectedAuto::equals)) {
				autonMode = selectedAuto;
			}
		}
		
		return String.join("\n", new String[]{
			"<h1>Autonomous Mode Selector</h1>",
			"<h3 id=\"current-mode\">Current Mode: " + String.valueOf(autonMode) + "</h1>",
			"<form>",
			"<p>" + HtmlElements.buildDropDown("AutoMode", autonMode, AUTONS) + "</p>",
			"<input type=\"submit\" value=\"Submit\"></form>",
			"<script>",
			"  function refresh() {",
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
			"    xhttp.open('GET', window.location.href, true);",
			"    xhttp.send();",
			"  }",
			"  setInterval(refresh, 1000);",
			"</script>",
		});
	}

	@Override
	public String title() {
		return "Autonomous Selector";
	}
}
