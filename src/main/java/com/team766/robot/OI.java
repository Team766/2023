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

	boolean manualControl = true;
	
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

			// log("E1: " + Robot.arms.getEncoderDistanceOfArmOne());
			// log("E2: " + Robot.arms.getEncoderDistanceOfArmTwo());
			if(joystick0.getButtonPressed(14)){
				manualControl = true;
			} else if (joystick0.getButtonPressed(15)){
				manualControl = false;
				Robot.arms.manuallySetArmTwoPower(0);
				Robot.arms.manuallySetArmOnePower(0);
			}

			// if(manualControl == true){
			// 	Robot.arms.manuallySetArmTwoPower(joystick0.getAxis(0)*0.2);
			// 	Robot.arms.manuallySetArmOnePower(joystick0.getAxis(1) * 0.25);
			// }



			if(joystick0.getButtonPressed(1)){

				Robot.arms.resetEncoders();
			}

			if(joystick0.getButtonPressed(2)){
				Robot.arms.manuallySetArmOnePower(0);
				Robot.arms.manuallySetArmTwoPower(0);
			}



			// if(joystick0.getButton(3) && manualControl == false){
				
			// 	Robot.arms.pidForArmTwo(0.231);
			// 	log("3");
			// }

			if(joystick0.getButtonPressed(3)){
				Robot.arms.antiGravFirstJoint();
				log("AntiGravFirstJoint");
			}

			if(joystick0.getButtonPressed(4)){
				Robot.arms.antiGravSecondJoint();
				log("AntiGravSecondJoint");
			}

			
			// if(joystick0.getButton(6) && manualControl == false){
			// 	log("6");
			// 	Robot.arms.pidForArmOne(0.82);
			// }

			// if(joystick0.getButton(7) && manualControl == false){
			// 	log("7");
			// 	Robot.arms.pidForArmOne(0.89);
			// }

			if(joystick0.getButton(8) && manualControl == false){
				log("8");
				Robot.arms.pidForArmOne(0.216);
			}


			

		}
	}
}
