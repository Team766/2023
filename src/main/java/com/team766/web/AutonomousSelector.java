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
	public String handle(Map<String, Object> params) {
		String selectedAuto = (String)params.get("AutoMode");
		if (selectedAuto != null) {
			if (Arrays.stream(AUTONS).anyMatch(selectedAuto::equals)) {
				autonMode = selectedAuto;
			}
		}
		
		String r = "<h1>Autonomous Mode Selector</h1>\n";
		r += "<h3>Current Mode: " + String.valueOf(autonMode) + "</h1>\n";
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
