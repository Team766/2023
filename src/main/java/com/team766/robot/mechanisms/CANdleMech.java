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
		public void test(){
			FireAnimation m_toAnimate = new FireAnimation(0.5, 0.7, LedCount, 0.7, 0.5);
			m_candle.animate(m_toAnimate);
		}
		public void endtest(){
			RainbowAnimation off = new RainbowAnimation(0, 0, 0);
			m_candle.animate(off);
		}

}