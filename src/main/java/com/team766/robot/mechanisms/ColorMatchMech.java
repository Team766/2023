package com.team766.robot.mechanisms;

import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorMatch;
import com.team766.framework.Mechanism;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;
//identifies game pieces by color & checks if cones are held right
//REMEMBER: note object colors as CONE, CUBE, or OTHER
public class ColorMatchMech extends Mechanism {
	private final ColorMatch m_colorMatcher = new ColorMatch();
	//private final I2C.Port i2cPort = /*???*/;
	//private final ColorSensorV3 m_colorSensor = new ColorSensorV3(/*i2cPort*/);
	private final I2C.Port i2cPort = I2C.Port.kOnboard;
	private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);
	//need to find rgb with sensor to get rgb vals
	private static final Color coneYellow = new Color(0.387, 0.56, 0.052);
  	private static final Color cubePurple = new Color(0.208, 0.31, 0.48);
	private final Color green = new Color(0.197, 0.561, 0.240);
	private final Color red = new Color(0.561, 0.232, 0.114);
	private final Color black = new Color(0.0,0.0,0.0);
	private final Color white = new Color(1.0,1.0,1.0);
	
	public ColorMatchMech(){

	}
	//adds possible colors
	
	public void makeColorMatches(){
		m_colorMatcher.addColorMatch(coneYellow);
		m_colorMatcher.addColorMatch(cubePurple);
		m_colorMatcher.addColorMatch(green);
		m_colorMatcher.addColorMatch(red);
		m_colorMatcher.addColorMatch(black);
		m_colorMatcher.addColorMatch(white);
		m_colorMatcher.setConfidenceThreshold(0.95);
	}
	//identifies held object by color
	public void checkColor(){
		Color detectedColor = m_colorSensor.getColor();
		String piece;
		ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);

		if(match.color == coneYellow){
			piece = "Cone";
		} else if (match.color == cubePurple){
			piece = "Cube";
		} else {
			piece = "Other";
		}
		log("piece: "+piece);
		//log("detected color: "+detectedColor);
		//log("color: "+match.color);
		//log("confidence: "+match.confidence);
	}
	/* 
	//returns if cone is in right position (cone base by sensor/base away from sensor/can't see base?)
	//check with CADders where sensor is excactly
	public String checkConeHold(){
		String conePos;
		//talk to CADders about which cone positions are good
		//make a proximity logger to find if-statement ranges
		//REMEMBER: proximity is from 0-2047; greater num = closer
		if(m_colorSensor.getProximity()>/*close proximities){
			conePos = "base at sensor";
		} else if(/*close prox>m_colorSensor.getProximity()>/*far prox){
			conePos = "base away from sensor";
		} else {
			conePos = "can't see base";
		}
		return conePos;
	}
	*/
}
