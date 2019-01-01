package com.team766.web;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.team766.config.AbstractConfigValue;
import com.team766.config.ConfigFileReader;

public class ConfigUI implements WebServer.Handler {
	private static String parseParamValue(Object in) {
		if ("".equals(in)) {
			return null;
		}
		return (String)in;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public String handle(Map<String, Object> params) {
		Map<String, String> stringParams =
				params.entrySet().stream()
				.map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), parseParamValue(entry.getValue())))
				.filter(entry -> !entry.getKey().equals("saveToFile") && entry.getValue() != null)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		ArrayList<String> validationErrors = new ArrayList<String>();
		for (Entry<String, String> param : stringParams.entrySet()) {
			if (param.getValue().isEmpty()) {
				continue;
			}
			try {
				AbstractConfigValue.accessedValues().get(param.getKey()).parseValue(param.getValue());
			} catch (Exception ex) {
				validationErrors.add(String.format("Could not parse config value for %s: %s", param.getKey(), ex.toString()));
			}
		}
		if (validationErrors.isEmpty()) {
			ConfigFileReader.getInstance().setValues(stringParams);
			if (params.containsKey("saveToFile")) {
				try {
					ConfigFileReader.getInstance().saveToFile();
				} catch (IOException ex) {
					validationErrors.add("Could not save config file: " + ex.toString());
				}
			}
		}
		
		String r = "<h1>Configuration</h1>";
		if (validationErrors.size() > 0) {
			r += "<p>Errors:\n";
			r += "<ul>\n";
			for (String error : validationErrors) {
				r += "<li>" + error + "</li>\n";
			}
			r += "</ul></p>";
		}
		r += "<form method=\"post\">\n";
		r += "<table border=\"1\">\n";
		for (Entry<String, AbstractConfigValue> entry : new TreeMap<>(AbstractConfigValue.accessedValues()).entrySet()) {
			r += String.format("<tr><td>%1$s</td><td><input type=\"text\" name=\"%1$s\" value=\"%2$s\"</td></tr>", entry.getKey(), entry.getValue());
		}
		r += "</table>\n";
		r += "<p>Save to config file? <input type=\"checkbox\" name=\"saveToFile\"></p>\n";
		r += "<p><input type=\"submit\" value=\"Apply\"></p></form>\n";
		
		return r;
	}

	@Override
	public String title() {
		return "Config Values";
	}
}
