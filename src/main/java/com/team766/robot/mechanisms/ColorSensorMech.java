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
		
	}
	//RGB MIGHT BE 16 BIT
	public void senseRGB(){
		Color detectedColor = m_colorSensor.getColor();
		
		log("detected color: "+detectedColor);
		log("red: "+detectedColor.red+" green: "+detectedColor.green+" blue: "+detectedColor.blue);
		
	}
	//since the prox sensor will basically return the same val if a piece is >4 cm away, this code just says if something is right in front of it or not
	public void senseProx(){
		int prox = m_colorSensor.getProximity();
		
		if(prox<200){
			log("object is out of range");
		} else {
			log("sensing object :)");
		}
		
	}
	
}
