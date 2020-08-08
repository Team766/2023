package com.team766.web;

import java.util.Arrays;
import java.util.Map;

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
		autonMode = AUTONS[0];
    }
	
	public String getSelectedAutonModeString() {
		return autonMode;
	}
	
	public <E extends Enum<E>> E getSelectedAutonMode(Class<E> clazz) {
		return Enum.valueOf(clazz, autonMode);
	}
	
	@Override
	public String handle(Map<String, Object> params) {
		String selectedAuto = (String)params.get("AutoMode");
		if (selectedAuto != null) {
			if (Arrays.stream(AUTONS).anyMatch(selectedAuto::equals)) {
				autonMode = selectedAuto;
			}
		}
		
		String r = "<h1>Autonomous Mode Selector</h1>\n";
		r += "<h3>Current Mode: " + autonMode + "</h1>\n";
		r += "<form>\n";
		r += "<p>" + HtmlElements.buildDropDown("AutoMode", autonMode, AUTONS) + "</p>\n";
		r += "<input type=\"submit\" value=\"Submit\"></form>\n";
		
		return r;
	}

	@Override
	public String title() {
		return "Autonomous Selector";
	}
}
