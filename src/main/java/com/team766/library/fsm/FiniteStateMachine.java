package com.team766.library.fsm;

import java.util.HashSet;
import com.team766.logging.Category;
import com.team766.logging.Logger;

/**
 * A simple FSM container
 * 
 * - Imnplementation is not totally foolproof, human error may still violate state expectations
 *  TODO: add more checks, guards
 *  TODO: we should really have an FSM-specific Exception container
 */
public abstract class FiniteStateMachine {
	private HashSet<FiniteState> states;
	private FiniteState currentState;
	private FiniteState nextState;

	protected Logger logger;

	public FiniteStateMachine() {
		logger = Logger.get(Category.FRAMEWORK);
	}

	public void initialize(HashSet<FiniteState> finiteStates, FiniteState initialState) throws Exception {
		this.states = finiteStates;

		this.currentState = null;
		this.nextState = null;

		switchState(initialState);
	}

	public FiniteState getCurrentState() {
		return currentState;
	}

	// Typically only the state can switch to other states, but we'll leave this as public
	public void switchState(FiniteState nextState) throws Exception {
		if(!states.contains(nextState)) throw new Exception("provided state does not exist in list of valid states in this machine");
		this.nextState = nextState;
	}

	public void run() throws Exception {
		// switch state if there is something to switch to
		if(nextState != null) {
			if(currentState != null) currentState.onExitValidation(nextState);
			nextState.onEnterValidation(currentState);

			// if no exceptions thrown, we continue
			currentState.onExit();
			currentState = nextState;
			nextState = null;
			currentState.onEnter();
		}

		if(this.currentState != null) this.currentState.run();
	}
}
