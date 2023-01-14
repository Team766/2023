package com.team766.hal;

public class MotorControllerCommandFailedException extends RuntimeException {

	public MotorControllerCommandFailedException(String message) {
		super(message);
	}

	public MotorControllerCommandFailedException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
