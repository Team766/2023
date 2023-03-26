package com.team766.robot;

import java.io.IOException;

import org.apache.commons.math3.ode.FirstOrderConverter;

import com.team766.framework.Procedure;

import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;

import com.team766.robot.procedures.*;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {
	private JoystickReader joystick0;
	private JoystickReader joystick1;
	private JoystickReader joystick2;
	enum armControlTypes{
		MANUAL,
		READY,
		HIGH_NODE,
		MID_NODE,
		LOW_NODE,
		HUMANPLAYER_PICKUP
	};
	public armControlTypes state = armControlTypes.MANUAL;
	

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
			
			if(joystick0.getButton(1)){
				state = armControlTypes.HIGH_NODE;
				
			}

			if(joystick0.getButton(2)){
				state = armControlTypes.MID_NODE;
				
			}

			if(joystick0.getButton(3)){
				state = armControlTypes.LOW_NODE;
				
			}

			if(joystick0.getButton(6)){
				state = armControlTypes.HUMANPLAYER_PICKUP;
			}
			if(joystick0.getButton(7)){
				state = armControlTypes.MANUAL;
			}

			switch(state){
				case MANUAL:
					Robot.arms.manuallySetArmOnePower(joystick0.getAxis(0));
					Robot.arms.manuallySetArmTwoPower(joystick0.getAxis(1));
					break;
				case HIGH_NODE:
					Robot.arms.pidForArmOne(0);
					Robot.arms.pidForArmTwo(0);
					break;
				case MID_NODE:
					Robot.arms.pidForArmOne(0);
					Robot.arms.pidForArmTwo(0);
					break;
				case LOW_NODE:
					Robot.arms.pidForArmOne(0);
					Robot.arms.pidForArmTwo(0);
					break;
				case READY:
					Robot.arms.pidForArmOne(0);
					Robot.arms.pidForArmTwo(0);
					break;
				case HUMANPLAYER_PICKUP:
					Robot.arms.pidForArmOne(0);
					Robot.arms.pidForArmTwo(0);
					break;
			}
		}
	}
}
