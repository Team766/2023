package com.team766.robot;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.robot.mechanisms.*;

import com.team766.logging.Category;
import com.team766.robot.procedures.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.math.geometry.Pose3d;


/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {
	private JoystickReader joystick0;
	private JoystickReader joystick1;
	private JoystickReader joystick2;
	private CameraServer cameraserver;
	public OI() {
		loggerCategory = Category.OPERATOR_INTERFACE;

		joystick0 = RobotProvider.instance.getJoystick(0);
		joystick1 = RobotProvider.instance.getJoystick(1);
		joystick2 = RobotProvider.instance.getJoystick(2);
		cameraserver.startAutomaticCapture();
	}
	
	public void run(Context context) {
		context.takeOwnership(Robot.drive);
		//context.takeOwnership(Robot.photonVision);
		
		while (true) {
			// wait for driver station data (and refresh it using the WPILib APIs)
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
			RobotProvider.instance.refreshDriverStationData();

			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.

			Robot.drive.setArcadeDrivePower(joystick0.getAxis(2), -1*joystick0.getAxis(1));
			//log("Is there a target? " + Robot.photonVision.hasTarget());
			//log the x,y,z, and angle of the target
			try {
				Pose3d pose = Robot.photonVision.getPose3d();
				if(pose != null){
					log("X: " + pose.getX() + "\n Y: " + pose.getY() + "\n Z: " + pose.getZ());
				} else{
					log("No pose");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
