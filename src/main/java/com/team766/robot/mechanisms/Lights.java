package com.team766.robot.mechanisms;
import com.ctre.phoenix.led.CANdle;
import com.team766.framework.Mechanism;


public class Lights extends Mechanism{

	private CANdle candle;

	public Lights(){
		candle = new CANdle(5);

	}

	public void purple(){
		checkContextOwnership();
		candle.setLEDs(128, 0, 128);
	}

	public void white(){
		checkContextOwnership();
		candle.setLEDs(255, 255, 255);
	}

	public void yellow(){
		checkContextOwnership();
		candle.setLEDs(255, 255, 0);
	}
}
