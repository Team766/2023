package com.team766.hal;

public class SpeedControllerCommandFailedException extends RuntimeException {

	public SpeedControllerCommandFailedException(String message) {
		super(message);
	}

	public SpeedControllerCommandFailedException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
