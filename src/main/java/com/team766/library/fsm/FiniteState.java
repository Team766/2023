package com.team766.library.fsm;

import com.team766.logging.Category;
import com.team766.logging.Logger;

/**
 * A state which belongs to part of a StateMachine
 * 
 * - Imnplementation is not totally foolproof, human error may still violate state expectations
 * TODO: add more checks, guards
 */
public abstract class FiniteState {

	protected Logger logger;

	public FiniteState() {
		logger = Logger.get(Category.FRAMEWORK);
	}
	

	// Checks for entering this state e.g. whitelist valid states
	abstract public void onEnterValidation(FiniteState previousState) throws Exception;

	// Checks for exiting this state e.g. whitelist valid states, conditions, etc
	abstract public void onExitValidation(FiniteState nextState) throws Exception;


	abstract public void onEnter() throws Exception;

	abstract public void onExit() throws Exception;

	abstract public void run() throws Exception;
}
