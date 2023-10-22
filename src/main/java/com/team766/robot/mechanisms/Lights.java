package com.team766.robot.mechanisms;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.StrobeAnimation;
import com.team766.framework.Mechanism;


public class Lights extends Mechanism {

	public static class RGB {
		public int r;
		public int g;
		public int b;

		public RGB(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}

	private static final int CANID = 5;

	private final CANdle candle;
	private int numLEDs = 42;
	private RGB color;
	private boolean blink = false;

	public Lights() {
		candle = new CANdle(CANID);
	}

	private void handleLighting() {
		if (!blink) {
			candle.setLEDs(color.r, color.g, color.b);
		} else {
			candle.animate(new StrobeAnimation(color.r, color.g, color.b));
		}
	}

	public void signalCube() {
		checkContextOwnership();
		color = new RGB(128, 0, 128);
		handleLighting();
	}

	public void signalCone(){
		checkContextOwnership();
		color = new RGB(255, 255, 0);
		handleLighting();
	}

	public void signalLowNode() {
		checkContextOwnership();
		color = new RGB(255, 255, 255);
		handleLighting();
	}

	public void signalMidNode() {
		checkContextOwnership();
		color = new RGB(0, 255, 0);
		handleLighting();
	}

	public void signalHighNode() {
		checkContextOwnership();
		color = new RGB(0, 0, 255);
		handleLighting();		
	}

	public void setBlink(boolean blink) {
		checkContextOwnership();
		this.blink = blink;
		handleLighting();
	}
}
