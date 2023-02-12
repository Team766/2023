package com.team766.robot.mechanisms;

import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorMatch;
import com.team766.framework.Mechanism;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;


//identifies game pieces by color & checks if cones are held right

public class ColorMatchMech extends Mechanism {
	private final ColorMatch m_colorMatcher = new ColorMatch();

	private final I2C.Port i2cPort1 = I2C.Port.kOnboard;
	private final I2C.Port i2cPort2 = I2C.Port.kMXP;
	private final ColorSensorV3 m_colorSensorA = new ColorSensorV3(i2cPort1);
	private final ColorSensorV3 m_colorSensorB = new ColorSensorV3(i2cPort2);
	//sensor checks which of these colors its reading is closest to
	private static final Color coneYellow = new Color(0.387, 0.56, 0.052);
  	private static final Color cubePurple = new Color(0.208, 0.31, 0.48);
	private final Color green = new Color(0.197, 0.561, 0.240);
	private final Color red = new Color(0.561, 0.232, 0.114);
	private final Color black = new Color(0.0,0.0,0.0);
	private final Color white = new Color(1.0,1.0,1.0);
	private final Color boxTube1 = new Color(0.359,0.460,0.181);
	private final Color offWhite = new Color(0.381,0.463,0.157);
	
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
	public void checkColorA(){
		Color detectedColor = m_colorSensorA.getColor();
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
	//I know having a different method for each sensor isn't the best but i'm lazy
	public void checkColorB(){
		Color detectedColor = m_colorSensorB.getColor();
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

	public void senseProxA(){
		int prox = m_colorSensorA.getProximity();
		
		if(prox<200){
			log("object is out of range");
		} else {
			log("sensing object :)");
		}
		
	}

	public void senseProxB(){
		int prox = m_colorSensorB.getProximity();
		
		if(prox<200){
			log("object is out of range");
		} else {
			log("sensing object :)");
		}
		
	}
	
}
