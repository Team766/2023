package com.team766.robot.states;

import java.util.Arrays;
import java.util.HashSet;
import com.team766.library.fsm.FiniteState;
import com.team766.library.fsm.FiniteStateMachine;

/**
 * Note: Singleton FSM
 */
public class ArmControlFsm extends FiniteStateMachine {

	private static ArmControlFsm instance;

	public static ArmControlFsm getInstance() {
		return instance;
	}

	public FiniteState armControlManual;
	public FiniteState armControlAuto;

	private ArmControlFsm() throws Exception {
		super();

		// create states
		armControlAuto = new ArmAutomatedControlState();
		armControlManual = new ArmManualControlState();
	}

	
	public void initialize() throws Exception {
		super.initialize(
			new HashSet(
				Arrays.asList(armControlAuto, armControlManual)),
			armControlAuto);
	}
}
