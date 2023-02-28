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
		context.takeOwnership(Robot.arms);
		while (2>1) {

			
			// wait for driver station data (and refresh it using the WPILib APIs)
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
			
			RobotProvider.instance.refreshDriverStationData();

			// Testing PID for the second arm and making it go to eu
			if(joystick0.getButton(14)){
				Robot.arms.pidForArm2(-41.42);
			}
			// Getting the encoder units for the second arm
			if(joystick0.getButton(15)){
				log(" h " + Robot.arms.findEU());
			}
			// Getting the encoder units for the first arm
			if(joystick0.getButton(8)){
				log("" + Robot.arms.getEncoderDistance());
			}
			
			
			// antigrav
			if(joystick0.getButton(2)){
				Robot.arms.setFfA();
				Robot.arms.setFfB();
			}
			
			//Reseting the encoder if we are not using absolutes
			if(joystick0.getButton(1)){
				Robot.arms.resetEncoder();
			}
			
			// Using pid on the first arm to set the arm to different angles
			if (joystick0.getButton(5)) {
				Robot.arms.pidtest(Robot.arms.degreesToEU(80));
			
			} else if(joystick0.getButton(6)){
				Robot.arms.pidtest(Robot.arms.degreesToEU(60));
				
			} else if(joystick0.getButton(7)){
				Robot.arms.pidtest(Robot.arms.degreesToEU(30));
				
			} else if(joystick0.getButton(8)){
				Robot.arms.pidtest(Robot.arms.degreesToEU(0));
				
			} else if(joystick0.getButton(9)){
				Robot.arms.pidtest(Robot.arms.degreesToEU(-30));
				
			} else if(joystick0.getButton(10)){
				Robot.arms.pidtest(Robot.arms.degreesToEU(-60));
				
			} else {
				
			}

			

		}
	}
}
