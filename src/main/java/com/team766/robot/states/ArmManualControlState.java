package com.team766.robot.states;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.library.fsm.FiniteState;
import com.team766.logging.Severity;
import com.team766.robot.Robot;

public class ArmManualControlState extends FiniteState {

	private JoystickReader debugJoystick;

	public ArmManualControlState() {
		super();
		
		debugJoystick = RobotProvider.instance.getJoystick(0);
	}

	private HashSet<Type> allowedTransitions = new HashSet<Type> (Arrays.asList(ArmAutomatedControlState.class));

	@Override
	public void onEnterValidation(FiniteState previousState) throws Exception {
		if(!allowedTransitions.contains(previousState.getClass())) throw new Exception("Invalid enter transition from previous state");
	}

	@Override
	public void onExitValidation(FiniteState nextState) throws Exception {
		if(!allowedTransitions.contains(nextState.getClass())) throw new Exception("Invalid transition to next state");
	}

	@Override
	public void onEnter() throws Exception  {
		logger.logRaw(Severity.WARNING, ">>> State : ArmManualControlState Entered");
	}

	@Override
	public void onExit() throws Exception {
		logger.logRaw(Severity.WARNING, ">>> State : ArmManualControlState Exited");
	}

	@Override
	public void run() throws Exception {

		double axisValue = debugJoystick.getAxis(1);

		// scale degrees per (rough) iteration from -1 thru 1 to ...
		double angularIncrement = axisValue * 10.0d;
		
		double motorAngle = Robot.arms.firstJoint.getMotorPosition();
		Robot.arms.firstJoint.setMotorPosition(motorAngle + angularIncrement);
		
	}
	
}
