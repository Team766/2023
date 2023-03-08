package com.team766.robot.states;

import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.library.states.ProcedureState;
import com.team766.library.states.ProcedureStateException;
import com.team766.logging.Severity;
import com.team766.robot.Robot;

public class ArmManualControlState extends ProcedureState {

	private JoystickReader debugJoystick;

	public ArmManualControlState() {
		super();
		
		debugJoystick = RobotProvider.instance.getJoystick(0);
	}

	@Override
	public void onEnterValidation(ProcedureState previousState) throws ProcedureStateException {
		// allow enter from null (initial) state
		if(previousState == null) return;
		if(previousState instanceof ArmAutomatedControlState) return;
		throw new ProcedureStateException("Invalid enter transition from previous state");
	}

	@Override
	public void onExitValidation(ProcedureState nextState) throws ProcedureStateException {
		if(nextState instanceof ArmAutomatedControlState) return;
		throw new ProcedureStateException("Invalid transition to next state");
	}

	@Override
	public void onEnter() throws ProcedureStateException  {
		log(Severity.WARNING, ">>> State : ArmManualControlState Entered");
	}

	@Override
	public void onExit() throws ProcedureStateException {
		log(Severity.WARNING, ">>> State : ArmManualControlState Exited");
	}

	@Override
	public void run(Context ctx) {

		double axisValue = debugJoystick.getAxis(1);

		// scale degrees per (rough) iteration from -1 thru 1 to ...
		double angularIncrement = axisValue * 10.0d;
		
		double motorAngle = Robot.arms.firstJoint.getMotorPosition();
		Robot.arms.firstJoint.setMotorPosition(motorAngle + angularIncrement);
		
	}
	
}
