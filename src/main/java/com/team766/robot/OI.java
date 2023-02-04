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
		context.takeOwnership(Robot.pulley);
		while (true) {
			
			// wait for driver station data (and refresh it using the WPILib APIs)
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
			
			RobotProvider.instance.refreshDriverStationData();
			if(joystick0.getButton(8)){
				log("" + Robot.pulley.getEncoderDistance());
			}
			if(joystick0.getButton(9)){
				//Robot.pulley.setPosition(6600);
			}
					Robot.pulley.setPulleyPower(joystick0.getAxis(1)*.3);
			
			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.
			//ignore nightmarish variable names
			//When you pronounce hiiiii, you must alot 1 second for each i.
			log("button: "+joystick0.getButton(8));
			
			boolean hiiiii = joystick0.getButton(8);
				while(hiiiii) {
					log("hiiiiiii");
					log("button in loop: "+joystick0.getButton(8));
					if (Robot.pulley.getEncoderDistance() > 6500 && Robot.pulley.getEncoderDistance() < 6700) {
						Robot.pulley.setPulleyPower(0);
					} else if (Robot.pulley.getEncoderDistance() < 6500) {
						Robot.pulley.setPulleyPower(0.12);
					} else {
						Robot.pulley.setPulleyPower(-0.1);
					}
					hiiiii=false;
				}
			
		}
	}
}
