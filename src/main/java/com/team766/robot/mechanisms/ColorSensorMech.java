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
		log("raw proximity value:"+m_colorSensor.getProximity());
		double calcProx = Math.round(((2047-(double)m_colorSensor.getProximity())/2047)*9);
		if(calcProx == 0){
			calcProx++;
		}
		log("calculated prox: "+calcProx+" - "+(calcProx+1)+" cm");
		/*
		int prox = m_colorSensor.getProximity();
		if(prox <= 227){
			log("calculated prox: 9-10 cm");
		} else if(prox <= 455){
			log("calculated prox: 8-9 cm");
		} else if(prox <= 682){
			log("calculated prox: 7-8 cm");
		} else if(prox <= 910){
			log("calculated prox: 6-7 cm");
		} else if(prox <= 1137){
			log("calculated prox: 5-6 cm");
		} else if(prox <= 1365){
			log("calculated prox: 4-5 cm");
		} else if(prox <= 1592){
			log("calculated prox: 3-4 cm");
		} else if(prox <= 1819){
			log("calculated prox: 2-3 cm");
		} else {
			log("calculated prox: 1-2 cm");
		}
		*/	
	}
	
}
