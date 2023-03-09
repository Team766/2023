package com.team766.robot.mechanisms;

import java.util.ArrayList;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import com.team766.framework.Mechanism;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;

public class MultiplexedColorSensorV3 extends Mechanism {
  //setting all the possible colors the sensors could decide it is seeing (only coneYellow and cubePurple matter)
  private static final Color coneYellow = new Color(0.387, 0.56, 0.052);
  private static final Color cubePurple = new Color(0.208, 0.31, 0.48);
	private final Color green = new Color(0.197, 0.561, 0.240);
	private final Color red = new Color(0.561, 0.232, 0.114);
	private final Color black = new Color(0.0,0.0,0.0);
	private final Color white = new Color(1.0,1.0,1.0);
	private final Color boxTube1 = new Color(0.359,0.460,0.181);
	private final Color offWhite = new Color(0.381,0.463,0.157);

  private final int kMultiplexerAddress = 0x70;
  private final ColorMatch m_colorMatcher = new ColorMatch();
  // The multiplexer I2C is static because it needs to be used for ALL of the multiplexer sensors,
  // and so by making it static all sensors can access it.
  private static I2C multiplexer;
  // The actual sensor. All of the methods call this sensor to get the data.
  private ColorSensorV3 sensor;
  // What port on the multiplexer the color sensor is plugged into.
  private final int port;
  

  public MultiplexedColorSensorV3 (I2C.Port i2cPort, int port) {
    if (multiplexer == null) {
      multiplexer = new I2C(i2cPort, kMultiplexerAddress);
    }
    this.port = port;
    setChannel();
    makeColorMatches();
    sensor = new ColorSensorV3(i2cPort);
    
  }

  /*
   * Helper method. This just sets the multiplexer to the correct port before
   * using the color sensor.
   */
  private void setChannel() {
    multiplexer.write(kMultiplexerAddress, 1 << port);
  }

  //returns what the sensor sees: cone, piece, or other
  public String[] getPiece() {
    setChannel();
    
    //makeColorMatches() used to be here but was put in the constructor for efficiency. 
    
    Color detectedColor = sensor.getColor();
		String[] piece = new String[2];
		ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);

		if(match.color == coneYellow){
			piece[0] = "Cone";
		} else if (match.color == cubePurple){
			piece[0] = "Cube";
		} else {
			piece[0] = "Other";
		} 
		log("piece: "+piece[0]+" port: "+port);
    piece[1] = getProximity();
		return piece;
  }
  //sensor will return whether or not an object is <= ~4cm from it
  public String getProximity() {
    
    int prox = sensor.getProximity();
		String proxResult;
		if(prox<200){
			proxResult = "object is out of range";
			log("object is out of range "+port);
		} else {
			proxResult = "sensing object :-)";
			log("sensing object :-) "+port);
		}
		return proxResult;
  }
  //gives sensors options of how to identify colors they are seeing
  public void makeColorMatches(){
    setChannel();
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



}
