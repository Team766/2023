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
	private final I2C.Port i2cPort = /*???*/;
	private final ColorSensorV3 m_colorSensor = new ColorSensorV3(/*i2cPort*/);
	//need to find rgb with sensor to get rgb vals
	private final Color coneYellow = new Color(/*r, g, b*/);
  	private final Color cubePurple = new Color(/*r, g, b*/);
	
	public ColorMatchMech(){

	}
	//adds possible colors
	public void makeColorMatches(){
		m_colorMatcher.addColorMatch(coneYellow);
		m_colorMatcher.addColorMatch(cubePurple);
	}
	//identifies held object by color
	public String checkColor(){
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
	}
	//returns if cone is in right position (cone base by sensor/base away from sensor/can't see base?)
	//check with CADders where sensor is excactly
	public String checkConeHold(){
		String conePos;
		//talk to CADders about which cone positions are good
		//make a proximity logger to find if-statement ranges
		//REMEMBER: proximity is from 0-2047; greater num = closer
		if(m_colorSensor.getProximity()>/*close proximities*/){
			conePos = "base at sensor";
		} else if(/*close prox*/>m_colorSensor.getProximity()>/*far prox*/){
			conePos = "base away from sensor";
		} else {
			conePos = "can't see base";
		}
		return conePos;
	}
}
