package com.team766.robot.mechanisms;

import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorMatch;
import com.team766.framework.Mechanism;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;


public class ColorSensorMech extends Mechanism {
	private final I2C.Port i2cPort = /*???*/;
	private final ColorSensorV3 m_colorSensor = new ColorSensorV3(/*i2cPort*/);
	//code just to grab rgb vals once
	public ColorSensorMech(){
		log(":-)");
	}
	public void senseRGB(){
		int lastColor[]= {m_colorSensor.getRed(), m_colorSensor.getGreen(), m_colorSensor.getBlue()};
			
				//code only logs if the sensor is getting a new color so it doesn't log a million times
				if((m_colorSensor.getRed()!=lastColor[0])||(m_colorSensor.getGreen()!=lastColor[1])||(m_colorSensor.getBlue()!=lastColor[2])){
					log("red: "+m_colorSensor.getRed()+" green: "+m_colorSensor.getGreen()+" blue: "+m_colorSensor.getBlue());
				}
				lastColor[0]=m_colorSensor.getRed();
				lastColor[1]=m_colorSensor.getGreen();
				lastColor[2]=m_colorSensor.getBlue();
		//:)
	}
	
}