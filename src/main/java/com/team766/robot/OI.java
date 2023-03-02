package com.team766.robot;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.procedures.*;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {
	private JoystickReader joystick0;
	private JoystickReader joystick1;
	private JoystickReader joystick2;
	
	public OI() {
		loggerCategory = Category.OPERATOR_INTERFACE;

		joystick0 = RobotProvider.instance.getJoystick(0);
		joystick1 = RobotProvider.instance.getJoystick(1);
		joystick2 = RobotProvider.instance.getJoystick(2);
	}
	
	public void run(Context context) {
		while (true) {
			// wait for driver station data (and refresh it using the WPILib APIs)
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
			RobotProvider.instance.refreshDriverStationData();

			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.
			context.takeOwnership(Robot.topColorSensor);
			context.takeOwnership(Robot.bottomColorSensor);
			Robot.topColorSensor.getPiece();
			Robot.bottomColorSensor.getPiece();
			Robot.topColorSensor.getProximity();
			Robot.bottomColorSensor.getProximity();

			//determines cone orientation
			//i have no idea where to put this so it's in oi for now...
			boolean topColor = Robot.topColorSensor.getPiece() != "Other";
			boolean bottomColor = Robot.bottomColorSensor.getPiece() != "Other";
			String orientation = "no piece";
			if(Robot.topColorSensor.getPiece() == "Cone" || Robot.bottomColorSensor.getPiece() == "Cone"){
				orientation = "top first";
				/*
				* When the cone comes in "base first," the bottom sensor could see the base for a split second before the top 
				* sensor does and accidentaly decide that the cone is moving in "top first" (because when the cone ACTUALLY
				* enters base first, only the bottom sensoor sees it). Hopefully checking if the bottom sensor is seeing
				* the cone very close up will deal with this issue, because the cone should (almost always) only be close to
				* the bottom sensor if it is the base. I have to test this. Thank you for reading my paragraph-long comment.
				*/
				if((topColor && bottomColor)||(!topColor && Robot.bottomColorSensor.getProximity() == "sensing object :-)")){
					orientation = "base first";
				}
			}
			if(Robot.topColorSensor.getPiece() == "Cube" || Robot.bottomColorSensor.getPiece() == "Cube"){
				orientation = "cube";
			}
		}
	}
}
