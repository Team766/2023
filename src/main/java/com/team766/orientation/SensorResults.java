package com.team766.orientation;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.procedures.*;
import edu.wpi.first.wpilibj.DriverStation;
import com.team766.robot.Robot;

//all-purpose class for when other mechanisms need cone orientation
//this probably shouldn't be in mechanisms
public class SensorResults {

	public SensorResults(){
		
	}

	//determines cone orientation
	public static int getConeOrientation(){
		String topPiece = Robot.topColorSensor.getPiece();
		String bottomPiece = Robot.bottomColorSensor.getPiece();
		String bottomProx = Robot.bottomColorSensor.getProximity();
		boolean topColor = !topPiece.equals("Other");
		boolean bottomColor = !bottomPiece.equals("Other");
		int orientation = 0; // orientation 0 = undetermined orientation
		if(topPiece.equals("Cone") || bottomPiece.equals("Cone")){
			//if only 1 sensor sees the cone, the top of the cone is coming in first
			orientation = 1; // orientation 1 = Robot.topColorSensor first
			/*
			* When the cone comes in "base first," the Robot.bottomColorSensor sensor could see the base for a split second before the Robot.topColorSensor 
			* sensor does and accidentaly decide that the cone is moving in "Robot.topColorSensor first" (because when the cone ACTUALLY
			* enters base first, only the Robot.bottomColorSensor sensoor sees it). Hopefully checking if the Robot.bottomColorSensor sensor is seeing
			* the cone very close up will deal with this issue, because the cone should (almost always) only be close to
			* the Robot.bottomColorSensor sensor if it is the base. I have to test this. Thank you for reading my paragraph-long comment.
			*/
			//if both sensors see the cone or the bottom one sees the base, the base of the cone is coming in first
			if((topColor && bottomColor)||(!topColor && bottomProx.equals("sensing object :-)"))){
				orientation = 2; // orientation 2 = base first
			}
		}

		if(topPiece.equals("Cube") || bottomPiece.equals("Cube")){
			orientation = 3; // orientation 3 = cube
		}
		
		return orientation;

	}

}