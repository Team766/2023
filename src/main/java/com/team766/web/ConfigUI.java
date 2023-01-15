package com.team766.web;

import java.util.ArrayList;
import java.util.Map;
import com.team766.config.ConfigFileReader;
import com.team766.config.ConfigValueParseException;

public class ConfigUI implements WebServer.Handler {
	@Override
	public String endpoint() {
		return "/config";
	}

	@Override
	public String handle(Map<String, Object> params) {
		String r = "<h1>Configuration (READ ONLY)</h1>";
		r += "<p>";
		r += "To edit, copy contents and place in <code>src/main/deploy/configs</code> directory.<br/>";
		r += "See <a href=\"https://docs.google.com/document/d/1eKOG5iWi2tijRls1L9qZ4xBZx50y1EriIk2QBUTv86g/edit#\">docs</a> for more information.<br/>";
		r += "</p>";
		
		// TODO: handle empty config file case.
		r += "<textarea name=\"configJson\" readonly disabled style=\"background: lightgray; box-sizing: border-box; width: 100%; height: 400px;\">";
		r += ConfigFileReader.getInstance().getJsonString();
		r += "</textarea>\n";
		return r;
	}

	@Override
	public String title() {
		return "Config Values";
	}
}
