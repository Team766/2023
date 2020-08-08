package com.team766.hal.mock;

import com.team766.hal.RelayOutput;

public class Relay implements RelayOutput{

	private Value val;
	
	public Relay(int port){
		val = Value.kOff;
	}
	
	@Override
	public void set(Value out) {
		val = out;
	}
	
	public Value get(){
		return val;
	}
	
}
