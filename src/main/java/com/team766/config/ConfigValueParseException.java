package com.team766.config;

public class ConfigValueParseException extends RuntimeException {
	private static final long serialVersionUID = -3235627203813966130L;
	
	public ConfigValueParseException(String message) {
		super(message);
	}

	public ConfigValueParseException(String message, Throwable cause) {
		super(message, cause);
	}
}