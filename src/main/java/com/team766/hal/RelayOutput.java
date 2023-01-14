package com.team766.hal;


/**
 * Interface for digital output devices
 */

public interface RelayOutput {
	
	public void set(Value val);
		
	enum Value{
		kOff, kOn, kForward, kReverse
	}
	
}
