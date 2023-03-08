package com.team766.library.states;

import java.util.HashSet;
import com.team766.framework.Context;

/**
 * A simple Finite State Machine container based on Procedure
 * 
 * - Imnplementation is not totally foolproof, human error may still violate state expectations
 *  TODO: add more checks, guards
 *  TODO: we should really have an FSM-specific Exception container
 */
public class ProcedureStateExecutor {
	private HashSet<ProcedureState> states;
	private ProcedureState currentState;
	private ProcedureState nextState;

	public ProcedureStateExecutor() {
	}

	public void initialize(HashSet<ProcedureState> finiteStates, ProcedureState initialState) throws Exception {
		this.states = finiteStates;

		this.currentState = null;
		this.nextState = null;

		switchState(initialState);
	}

	public ProcedureState getCurrentState() {
		return currentState;
	}

	// Typically only the state can switch to other states, but we'll leave this as public
	public void switchState(ProcedureState nextState) throws ProcedureStateException {
		// ignore if state == current state
		if(nextState == currentState) return;
		if(!states.contains(nextState)) throw new ProcedureStateException("provided state does not exist in list of valid states in this machine");
		this.nextState = nextState;
	}

	public void run(Context ctx) throws Exception {
		// switch state if there is something to switch to
		if(nextState != null) {
			if(currentState != null) currentState.onExitValidation(nextState);
			nextState.onEnterValidation(currentState);

			// if no exceptions thrown, we continue
			if(currentState != null) currentState.onExit();
			currentState = nextState;
			nextState = null;
			currentState.onEnter();
		}

		if(this.currentState != null) this.currentState.run(ctx);
	}
}
