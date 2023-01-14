package com.team766.robot;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.robot.mechanisms.*;

import com.team766.logging.Category;
import com.team766.robot.procedures.*;

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
			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.
			context.takeOwnership(Robot.drive);
			context.takeOwnership(Robot.photonVision);
			Robot.drive.setArcadeDrivePower(joystick0.getAxis(1), joystick1.getAxis(0));
			log("Is there a target? " + Robot.photonVision.hasTarget());
			//log the x,y,z, and angle of the target
			if(Robot.photonVision.getXYZAngle() == null){
				log("problem, no target");
			} else{
			log("x "+ Robot.photonVision.getXYZAngle().get(0));
			log("y "+ Robot.photonVision.getXYZAngle().get(1));
			log("z "+ Robot.photonVision.getXYZAngle().get(2));
			log("angle "+ Robot.photonVision.getXYZAngle().get(3));
			}
			// Wait until the next update from the driver station.
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
		}
	}
}
