package com.team766.robot.states;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import com.team766.library.fsm.FiniteState;
import com.team766.library.fsm.FiniteStateMachine;
import com.team766.logging.Severity;

public class ArmAutomatedControlState extends FiniteState {

	public ArmAutomatedControlState() {
		super();
		//TODO Auto-generated constructor stub
	}

	@Override
	public void onEnterValidation(FiniteState previousState) throws Exception {
		// allow enter from null (initial) state
		if(previousState == null) return;
		if(previousState instanceof ArmManualControlState) return;
		throw new Exception("Invalid enter transition from previous state");
	}

	@Override
	public void onExitValidation(FiniteState nextState) throws Exception {
		if(nextState instanceof ArmManualControlState) return;
		throw new Exception("Invalid transition to next state");
	}

	@Override
	public void onEnter() throws Exception {
		logger.logRaw(Severity.WARNING, ">>> State : ArmAutomatedControlState Entered");
	}

	@Override
	public void onExit() throws Exception {
		logger.logRaw(Severity.WARNING, ">>> State : ArmAutomatedControlState Exited");
	}

	@Override
	public void run() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
