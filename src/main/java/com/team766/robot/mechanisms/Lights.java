package com.team766.robot.mechanisms;
import org.apache.commons.math3.analysis.function.Ceil;
import org.apache.commons.math3.stat.correlation.StorelessCovariance;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.RainbowAnimation;
import com.ctre.phoenix.led.StrobeAnimation;
import com.ctre.phoenix.led.TwinkleAnimation;
import com.ctre.phoenix.led.TwinkleAnimation.TwinklePercent;
import com.team766.framework.Mechanism;
import edu.wpi.first.wpilibj.DriverStation;


public class Lights extends Mechanism{

	private CANdle candle;
	private static final int CANID = 5;
	private int numLEDs = 42;
	RainbowAnimation rainbowAnim = new RainbowAnimation(1, 5, numLEDs);
	
	int curAnimation = -1;
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
		if(DriverStation.getMatchTime() > 30){
			if(curAnimation != -1){candle.clearAnimation(curAnimation);}
			candle.setLEDs(128, 0, 128);
		}else{
			candle.clearAnimation(curAnimation);
			if(DriverStation.getMatchTime() > 15){
				if((int) (DriverStation.getMatchTime() * 2) % 2 == 0){
					candle.setLEDs(128, 0, 128);
				}else{
					candle.setLEDs(0, 0, 0);
				}
			}else{
				if((int) (DriverStation.getMatchTime() * 4) % 2 == 0){
					candle.setLEDs(128, 0, 128);
				}else{
					candle.setLEDs(0, 0, 0);
				}
			}
		}
	}

	public void resetLights(){
		checkContextOwnership();
		candle.setLEDs(255, 255, 255);
	}

	public void signalCone(){
		checkContextOwnership();
		if(DriverStation.getMatchTime() > 30){
			if(curAnimation != -1){candle.clearAnimation(curAnimation);}
			candle.setLEDs(255, 255, 0);
		}else{
			candle.clearAnimation(curAnimation);
			if(DriverStation.getMatchTime() > 15){
				if((int) (DriverStation.getMatchTime() * 2) % 2 == 0){
					candle.setLEDs(225, 225, 0);
				}else{
					candle.setLEDs(0, 0, 0);
				}
			}else{
				if((int) (DriverStation.getMatchTime() * 4) % 2 == 0){
					candle.setLEDs(225,225,0);
				}else{
					candle.setLEDs(0, 0, 0);
				}
			}
		}

	}

	public void auton(){
		candle.setLEDs(0, 255, 0);
	}
	public void signalMalfunction(){
		checkContextOwnership();
		candle.setLEDs(255, 0, 0);
	}

	public void rainbow(){
		checkContextOwnership();
		candle.clearAnimation(curAnimation);
		candle.animate(rainbowAnim,0);
		curAnimation = 0;
		
	}

	public void clearAnimation(){
		checkContextOwnership();
		if(curAnimation != -1){
			candle.clearAnimation(curAnimation);
			curAnimation = -1;
		}
		
		
		
	}

	public void hybridScore(){
		checkContextOwnership();

		if(DriverStation.getMatchTime() > 30){
			if(curAnimation != -1){candle.clearAnimation(curAnimation);}
			candle.setLEDs(165, 128, 65);
		}else{
			candle.clearAnimation(curAnimation);
			if(DriverStation.getMatchTime() > 15){
				if((int) (DriverStation.getMatchTime() * 2) % 2 == 0){
					candle.setLEDs(165, 128, 65);
				}else{
					candle.setLEDs(0, 0, 0);
				}
			}else{
				if((int) (DriverStation.getMatchTime() * 4) % 2 == 0){
					candle.setLEDs(165, 128, 65);
				}else{
					candle.setLEDs(0, 0, 0);
				}
			}
		}
	}

	//color of justin and kapils shirts
	public void midScore(){
		checkContextOwnership();

		if(DriverStation.getMatchTime() > 30){
			if(curAnimation != -1){candle.clearAnimation(curAnimation);}
			candle.setLEDs(81,102,52);
		}else{
			candle.clearAnimation(curAnimation);
			if(DriverStation.getMatchTime() > 15){
				if((int) (DriverStation.getMatchTime() * 2) % 2 == 0){
					candle.setLEDs(81,102,52);
				}else{
					candle.setLEDs(0, 0, 0);
				}
			}else{
				if((int) (DriverStation.getMatchTime() * 4) % 2 == 0){
					candle.setLEDs(81,102,52);
				}else{
					candle.setLEDs(0, 0, 0);
				}
			}
		}
	}

	public void highScore(){
		checkContextOwnership();

		if(DriverStation.getMatchTime() > 30){
			if(curAnimation != -1){candle.clearAnimation(curAnimation);}
			candle.setLEDs(202, 39, 75);
		}else{
			candle.clearAnimation(curAnimation);
			if(DriverStation.getMatchTime() > 15){
				if((int) (DriverStation.getMatchTime() * 2) % 2 == 0){
					candle.setLEDs(202, 39, 75);
				}else{
					candle.setLEDs(0, 0, 0);
				}
			}else{
				if((int) (DriverStation.getMatchTime() * 4) % 2 == 0){
					candle.setLEDs(202, 39, 75);
				}else{
					candle.setLEDs(0, 0, 0);
				}
			}
		}
	}

}
