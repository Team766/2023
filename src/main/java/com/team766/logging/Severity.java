package com.team766.logging;

public enum Severity {
	MIN_SEVERITY,
	DEBUG,
	INFO,
	WARNING,
	ERROR;
	
	private static final Severity[] VALUES = Severity.values();
	public static Severity fromInteger(byte x) {
		return VALUES[x];
	}
}
