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


	enum generalControl{
		CONE_HIGH_NODE,
		CUBE_HIGH_NODE,
		CONE_MID_NODE,
		CUBE_MID_NODE,
		OFF,
		READY,
		HUMANPLAYER_PICKUP,
		MANUAL,
		HYBRID_NODE
	};
	


	public generalControl generalState = generalControl.OFF;


	
	
	
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
			//Uses joystick buttons to change states
			if(joystick0.getButton(1)){
				Robot.arms.resetEncoders();
			}

			if(joystick0.getButton(2)){
				Robot.arms.antiGravBothJoints();
			}

			if(joystick0.getButton(3)){
				//HIGH NODE SCORING
				Robot.arms.pidForArmOne(-38.34);
				Robot.arms.pidForArmTwo(-90.665);
			}
			if(joystick0.getButtonPressed(4)){
				//MID NODE SCORING
				Robot.arms.pidForArmOne(null);
				Robot.arms.pidForArmTwo(null);
			}

			if(joystick0.getButton(5)){
				//TAKING FROM HUMAN PLAYER
				Robot.arms.pidForArmOne(null);
				Robot.arms.pidForArmTwo(null);
			}

			if(joystick0.getButton(6)){
				//STOWED
				Robot.arms.pidForArmOne(null);
				Robot.arms.pidForArmTwo(null);
			}
			if(joystick0.getButton(7)){
				//READY
				Robot.arms.pidForArmOne(null);
				Robot.arms.pidForArmTwo(null);
			}
			if(joystick0.getButton(8)){
				//PREP
				Robot.arms.pidForArmOne(null);
				Robot.arms.pidForArmTwo(null);
			}

//TODO: can we stil use a switch?
/*
			switch(generalState){
				case OFF:
					log("generalControl is off");
					break;
				case CONE_HIGH_NODE:
					Robot.arms.pidForArmOne(0);
					Robot.arms.pidForArmTwo(0);
					break;
				case CUBE_HIGH_NODE:
					Robot.arms.pidForArmOne(0);
					Robot.arms.pidForArmTwo(0);
					break;
				case CONE_MID_NODE:
					Robot.arms.pidForArmOne(0);
					Robot.arms.pidForArmTwo(0);
					break;
				case CUBE_MID_NODE:
					Robot.arms.pidForArmOne(0);
					Robot.arms.pidForArmTwo(0);
					break;
				case MANUAL:
					Robot.arms.manuallySetArmOnePower(joystick0.getAxis(0));
					Robot.arms.manuallySetArmTwoPower(joystick0.getAxis(1));
					break;
				case READY:
					Robot.arms.pidForArmOne(0);
					Robot.arms.pidForArmTwo(0);
					break;
				case HUMANPLAYER_PICKUP:
					Robot.arms.pidForArmOne(0);
					Robot.arms.pidForArmTwo(0);
					break;
				case HYBRID_NODE:
					Robot.arms.pidForArmOne(0);
					Robot.arms.pidForArmTwo(0);
					break;
			}
			*/
		}
	}
}
