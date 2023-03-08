package com.team766.robot.states;

import java.util.Arrays;
import java.util.HashSet;
import com.team766.library.states.ProcedureState;
import com.team766.library.states.ProcedureStateExecutor;

/**
 * Note: Singleton FSM
 */
public class ArmControlFsm extends ProcedureStateExecutor {

	private static ArmControlFsm instance = new ArmControlFsm();

	public static ArmControlFsm getInstance() {
		return instance;
	}

	public ProcedureState armControlManual;
	public ProcedureState armControlAuto;

	private ArmControlFsm() {
		super();

		// create states
		armControlAuto = new ArmAutomatedControlState();
		armControlManual = new ArmManualControlState();
	}

	
	public void initialize() throws Exception {
		super.initialize(
			new HashSet(
				Arrays.asList(armControlAuto, armControlManual)),
			armControlManual);
	}
}
