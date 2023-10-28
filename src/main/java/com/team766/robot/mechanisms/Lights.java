package com.team766.robot.mechanisms;
import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.RainbowAnimation;
import com.team766.framework.Mechanism;
import com.team766.robot.constants.SwerveDriveConstants;


public class Lights extends Mechanism{

	private CANdle candle;
	private static final int CANID = 5;
	private static final int LED_COUNT = 90;
	private Animation rainbowAnimation = new RainbowAnimation(1, 1.5, LED_COUNT);

	public Lights(){
		candle = new CANdle(CANID, SwerveDriveConstants.SWERVE_CANBUS);

	}

	public void purple(){
		checkContextOwnership();
		candle.setLEDs(128, 0, 128);
	}

	public void white(){
		checkContextOwnership();
		// NOTE: 255, 255, 255 trips the breaker. lol
		candle.setLEDs(128, 128, 128);
	}

	public void yellow(){
		checkContextOwnership();
		candle.setLEDs(255, 150, 0);
	}

	public void red() {
		checkContextOwnership();
		candle.setLEDs(255, 0, 0);
	}

	public void green() {
		checkContextOwnership();
		candle.setLEDs(0, 255, 0);
	}

	public void orange() {
		checkContextOwnership();
		candle.setLEDs(255, 64, 0);
	}

	public void rainbow() {
		checkContextOwnership();
		candle.animate(rainbowAnimation);
	}
}
