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
	enum coneControl{
		OFF,
		HIGH_NODE,
		MID_NODE,
	};

	enum generalControl{
		OFF,
		READY,
		HUMANPLAYER_PICKUP,
		MANUAL,
		HYBRID_NODE
	};
	
	enum  cubeControl{
		OFF,
		HIGH_NODE,
		MID_NODE
	}

	public coneControl coneState = coneControl.OFF;
	public generalControl generalState = generalControl.OFF;
	public cubeControl cubeState = cubeControl.OFF;

	

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
			//Uses joystick buttons to change states
			if(joystick0.getButton(1)){
				generalControl = generalControl.OFF;
				cubeState = cubeControl.OFF;
				coneState = coneControl.HIGH_NODE;
			}

			if(joystick0.getButton(2)){
				generalState = generalControl.OFF;
				cubeState = cubeControl.OFF;
				coneState = coneControl.MID_NODE;
			}

			if(joystick0.getButton(3)){
				coneState = coneControl.OFF;
				cubeState = cubeControl.OFF;
				generalState = generalControl.HYBRID_NODE;
			}
			if(joystick0.getButton(4)){
				generalState = generalControl.OFF;
				coneState =  coneControl.OFF;
				cubeState = cubeControl.HIGH_NODE;
			}

			if(joystick0.getButton(6)){
				coneState = coneControl.OFF;
				cubeState = cubeControl.OFF;
				generalState = generalControl.HUMANPLAYER_PICKUP;
			}
			if(joystick0.getButton(7)){
				coneState = coneControl.OFF;
				cubeState = cubeControl.OFF;
				generalState = generalControl.MANUAL;
			}
			if(joystick0.getButton(8)){
				
			}

			switch(coneState){
				case HIGH_NODE:
					Robot.arms.pidForArmOne(0);
					Robot.arms.pidForArmTwo(0);
					break;
				case MID_NODE:
					Robot.arms.pidForArmOne(0);
					Robot.arms.pidForArmTwo(0);
					break;
			}
			switch(cubeState){
				case HIGH_NODE:
					Robot.arms.pidForArmOne(0);
					Robot.arms.pidForArmTwo(0);
					break;
				case MID_NODE:
					Robot.arms.pidForArmOne(0);
					Robot.arms.pidForArmTwo(0);
			}
			switch(generalState){
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
		}
	}
}
