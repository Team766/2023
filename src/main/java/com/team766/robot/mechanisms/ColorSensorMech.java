package com.team766.robot.mechanisms;

import com.revrobotics.ColorSensorV3;
import com.team766.framework.Mechanism;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;


public class ColorSensorMech extends Mechanism {
	private final I2C.Port i2cPort = I2C.Port.kOnboard;
	private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);
	//code just to grab rgb vals once
	public ColorSensorMech(){
		log(":-)");
	}
	//RGB MIGHT BE 16 BIT
	public void senseRGB(){
		Color detectedColor = m_colorSensor.getColor();
		//previous: int lastColor[]= {m_colorSensor.getRed(), m_colorSensor.getGreen(), m_colorSensor.getBlue()}
		log("detected color: "+detectedColor);
		log("red: "+detectedColor.red+" green: "+detectedColor.green+" blue: "+detectedColor.blue);
		//^have to see what the deal is with the dot notation colors --> just run it and check
		//previous: log("red: "+m_colorSensor.getRed()+" green: "+m_colorSensor.getGreen()+" blue: "+m_colorSensor.getBlue());
		//proposed change: omit				
		//previous: lastColor[0]=m_colorSensor.getRed();
		//previous: lastColor[1]=m_colorSensor.getGreen();
		//previous: lastColor[2]=m_colorSensor.getBlue();
	}
	//similar thing but for getting proximity ranges
	public void senseProx(){
		
		log("proximity from base:"+m_colorSensor.getProximity());
				
	}
	
}
