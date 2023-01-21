package com.team766.web;

import java.util.ArrayList;
import java.util.Map;
import com.team766.config.ConfigFileReader;
import com.team766.config.ConfigValueParseException;

public class PVlink implements WebServer.Handler {

	@Override
	public String endpoint() {
		return "http://10.7.66.11:5800/#/dashboard";
	}

	@Override
	public String title() {
		return "PhotonVision Dashboard";
	}

	@Override
	public String handle(Map<String, Object> params) {
		return "";
	}
	
}