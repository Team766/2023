package com.team766.web;

import java.util.ArrayList;
import java.util.Map;
import com.team766.config.ConfigFileReader;
import com.team766.config.ConfigValueParseException;

public class ConfigUI implements WebServer.Handler {
	@Override
	public String handle(Map<String, Object> params) {
		String r = "<h1>Configuration</h1>";

		if (params.containsKey("configJson")) {
			String configJsonString = (String)params.get("configJson");
			ArrayList<String> validationErrors = new ArrayList<String>();
			try {
				ConfigFileReader.getInstance().reloadFromJson(configJsonString);
			} catch (ConfigValueParseException ex) {
				validationErrors.add(ex.toString());
			} catch (Exception ex) {
				validationErrors.add("Failed to parse config json: " + ex);
			}
			if (validationErrors.isEmpty()) {
				r += "<p>New configuration (v" + ConfigFileReader.getInstance().getGeneration() + ") has been applied</p>";
				if (params.containsKey("saveToFile")) {
					try {
						ConfigFileReader.getInstance().saveFile(configJsonString);
					} catch (Exception ex) {
						validationErrors.add("Could not save config file: " + ex.toString());
					}
				}
			}
			if (validationErrors.size() > 0) {
				r += "<p>Errors:\n";
				r += "<ul>\n";
				for (String error : validationErrors) {
					r += "<li>" + error + "</li>\n";
				}
				r += "</ul></p>";
			}
		}
		
		r += "<form method=\"post\">\n";
		r += "<textarea name=\"configJson\" style=\"box-sizing: border-box; width: 100%; height: 400px;\">";
		if (params.containsKey("configJson")) {
			r += (String)params.get("configJson");
		} else {
			r += ConfigFileReader.getInstance().getJsonString();
		}
		r += "</textarea>\n";
		r += "<p>Save to config file on robot? <input type=\"checkbox\" name=\"saveToFile\"></p>\n";
		r += "<p><input type=\"submit\" value=\"Apply\"></p></form>\n";
		
		return r;
	}

	@Override
	public String title() {
		return "Config Values";
	}
}
