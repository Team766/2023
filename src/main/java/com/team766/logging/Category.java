package com.team766.logging;

public enum Category {
	// Only add to the end of this list
	JAVA_EXCEPTION,
	DRIVE,
	HAL,
	CAMERA,
	PID_CONTROLLER,
	TRAJECTORY,
	AUTONOMOUS,
	CONFIGURATION,
	PROCEDURES,
	OPERATOR_INTERFACE;
	
	private static final Category[] VALUES = Category.values();
	public static Category fromInteger(byte x) {
		return VALUES[x];
	}
}
