package com.team766.robot.mechanisms;

import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorMatch;
import com.team766.framework.Mechanism;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;


//identifies game pieces by color & checks if cones are held right
/*
 * 
 * 
 * THIS CODE WILL NOT BE USED FOR THE FINAL ROBOT!!!!!
 * 
 * 
*/

public class ColorMatchMech extends Mechanism {
	private final ColorMatch m_colorMatcher = new ColorMatch();
	//private final I2C.Port i2cPort = I2C.Port.kOnboard;
	private final ColorSensorV3 m_colorSensor = new ColorSensorV3(I2C.Port.kOnboard);
	//sensor checks which of these colors its reading is closest to
	private static final Color coneYellow = new Color(0.387, 0.56, 0.052);
  	private static final Color cubePurple = new Color(0.208, 0.31, 0.48);
	private final Color green = new Color(0.197, 0.561, 0.240);
	private final Color red = new Color(0.561, 0.232, 0.114);
	private final Color black = new Color(0.0,0.0,0.0);
	private final Color white = new Color(1.0,1.0,1.0);
	private final Color boxTube1 = new Color(0.359,0.460,0.181);
	private final Color offWhite = new Color(0.381,0.463,0.157);
	private ColorSensorV3 sensor;
	private int port = 0;
	
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
		m_colorMatcher.addColorMatch(boxTube1);
		m_colorMatcher.addColorMatch(offWhite);
		m_colorMatcher.setConfidenceThreshold(0.95);
	}
	//identifies held object by color
	public String checkColor(){
		
		Color detectedColor = sensor.getColor();
		String piece;
		ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);

		if(match.color == coneYellow){
			piece = "Cone";
		} else if (match.color == cubePurple){
			piece = "Cube";
		} else {
			piece = "Other";
		}
		log("piece: "+piece+" port: "+port);
		//log("detected color: "+detectedColor);
		//log("color: "+match.color);
		//log("confidence: "+match.confidence);
		return piece;
	}

	public String senseProx(){
		
		int prox = sensor.getProximity();
		String proxResult;
		if(prox<200){
			proxResult = "object is out of range";
			log("object is out of range");
		} else {
			proxResult = "sensing object :)";
			log("sensing object :)");
		}
		return proxResult;
	}

	
}
