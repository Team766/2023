package com.team766.robot.mechanisms;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.RainbowAnimation;
import com.team766.framework.Mechanism;


public class Lights extends Mechanism{

	private CANdle candle;
	private static final int CANID = 5;
	private int numLEDs = 8;
	RainbowAnimation rainbowAnim = new RainbowAnimation(1, 0.5, numLEDs);

	public Lights(){
		candle = new CANdle(CANID);

	}

	public void setNumLEDs(int num){
		checkContextOwnership();
		numLEDs = num;
		rainbowAnim.setNumLed(num);
	}

	public void signalCube(){
		checkContextOwnership();
		candle.setLEDs(128, 0, 128);
	}

	public void resetLights(){
		checkContextOwnership();
		candle.setLEDs(255, 255, 255);
	}

	public void signalCone(){
		checkContextOwnership();
		candle.setLEDs(255, 255, 0);
	}

	public void signalMalfunction(){
		checkContextOwnership();
		candle.setLEDs(255, 0, 0);
	}

	public void signalBalance(){
		checkContextOwnership();
		candle.animate(rainbowAnim);
	}
}
