package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController;

import com.ctre.phoenix.led.*;
import com.ctre.phoenix.led.CANdle.LEDStripType;
import com.ctre.phoenix.led.CANdle.VBatOutputMode;
import com.ctre.phoenix.led.ColorFlowAnimation.Direction;
import com.ctre.phoenix.led.LarsonAnimation.BounceMode;
import com.ctre.phoenix.led.TwinkleAnimation.TwinklePercent;
import com.ctre.phoenix.led.TwinkleOffAnimation.TwinkleOffPercent;

public class CANdleMech extends Mechanism {

	private final CANdle m_candle = new CANdle(5);
    private final int LedCount = 300;
	private int realr;
	private int realg;
	private int realb;
	

		public CANdleMech(){

		}

		public void setColor(double r, double g, double b){
			checkContextOwnership();
			realr = (int)((r)*255);
			realb = (int)((b)*255);
			realg = (int)((g)*255);
			m_candle.setLEDs((realr), (realg), (realb));
		}
		public void setColor(double r, double g, double b, int index){
			checkContextOwnership();
			realr = (int)((r)*255);
			realb = (int)((b)*255);
			realg = (int)((g)*255);


			m_candle.setLEDs((realr), (realg), (realb), 0, index, 1);
		}   
		/*private Animation m_toAnimate = null;
		public enum AnimationTypes {
			ColorFlow,
			Fire,
			Larson,
			Rainbow,
			RgbFade,
			SingleFade,
			Strobe,
			Twinkle,
			TwinkleOff,
			SetAll
		}
		private AnimationTypes m_currentAnimation;
		public void changeAnimation(AnimationTypes toChange) {
			checkContextOwnership();
			m_currentAnimation = toChange;
			

			m_toAnimate = new FireAnimation(0.5, 0.7, LedCount, 0.7, 0.5);

				
				/*case SetAll:
					m_toAnimate = null;
					break;*/
		
		//}
		public void FireAnim(){
			FireAnimation m_toAnimate = new FireAnimation(0.5, 0.7, LedCount, 0.7, 0.5);
			m_candle.animate(m_toAnimate);
		}
		public void ColorFlowAnim(){
			ColorFlowAnimation m_toAnim = new ColorFlowAnimation(128, 20, 70, 0, 0.7, LedCount, Direction.Forward);
			m_candle.animate(m_toAnim);
		}
		public void Larson(){
			LarsonAnimation m_toAni =  new LarsonAnimation(0, 255, 46, 0, 1, LedCount, BounceMode.Front, 3);
			m_candle.animate(m_toAni);
		}
		public void Rainbow(){
			RainbowAnimation m_toAn = new RainbowAnimation(1, 0.1, LedCount);
			m_candle.animate(m_toAn);
		}
		public void RgbFade(){
			RgbFadeAnimation m_toA = new RgbFadeAnimation(0.7, 0.4, LedCount);
			m_candle.animate(m_toA);
		}
		public void SingleFade(){
			SingleFadeAnimation m_to = new SingleFadeAnimation(50, 2, 200, 0, 0.5, LedCount);
			m_candle.animate(m_to);
		}
		public void Strobe(){
			StrobeAnimation m_t= new StrobeAnimation(240, 10, 180, 0, 98.0 / 256.0, LedCount);
			m_candle.animate(m_t);
		}
		public void Twinkle(){
			TwinkleAnimation m = new TwinkleAnimation(30, 70, 60, 0, 0.4, LedCount, TwinklePercent.Percent6);
			m_candle.animate(m);

		}
		public void EndAnim(){
			m_candle.setLEDs(0, 0, 0);
		}

}