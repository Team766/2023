package com.team766.robot;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.library.fsm.FiniteState;
import com.team766.library.fsm.FiniteStateMachine;
import com.team766.logging.Category;
import com.team766.logging.Severity;
import com.team766.robot.procedures.*;
import com.team766.robot.states.ArmAutomatedControlState;
import com.team766.robot.states.ArmControlFsm;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {

	private JoystickReader joystick0;

	private ArmControlFsm armControlFsm;

	public OI() {
		loggerCategory = Category.OPERATOR_INTERFACE;

		joystick0 = RobotProvider.instance.getJoystick(0);

		// create the fsm that links everything together
		armControlFsm = ArmControlFsm.getInstance();
	}
	
	public void run(Context context) {
		context.takeOwnership(Robot.arms);

		try {
			armControlFsm.initialize();
		} catch(Exception e) {
			log(Severity.ERROR, e.toString());
			log(Severity.ERROR, "Error initializing FSM");
			return;
		}

		while (true) {

			try {
				// TODO: we want to break this down to run the loop faster for animation / motor control purposes

				// wait for driver station data (and refresh it using the WPILib APIs)
				context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
				RobotProvider.instance.refreshDriverStationData();

				// Update all mechanisms
				Robot.arms.periodicUpdate();

				// switch between states depending on button press
				if(joystick0.getButtonPressed(5)) {
					armControlFsm.switchState(armControlFsm.armControlAuto);

				} else if(joystick0.getButtonPressed(6)) {
					armControlFsm.switchState(armControlFsm.armControlManual);
				}

				// zero the arm
				if(joystick0.getButtonPressed(7)) {
					log(Severity.INFO, getName(), "Encoders reset");
					Robot.arms.resetEncoders();
				}

				// run the current state

				armControlFsm.run();
				
			} catch (Exception e) {
				log(Severity.ERROR, e.toString());
			}

		}
	}
}
