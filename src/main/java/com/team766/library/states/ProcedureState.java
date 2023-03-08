package com.team766.library.states;

import com.team766.framework.Procedure;

/**
 * A state which belongs to part of a ProcedureStateExecutor
 * - This should only be run from ProcedureStateExecutor
 * 
 * - Imnplementation is not totally foolproof, human error may still violate state expectations
 * TODO: add more checks, guards
 */
public abstract class ProcedureState extends Procedure {

	public ProcedureState() {
	}
	
	// Checks for entering this state e.g. whitelist valid states
	abstract public void onEnterValidation(ProcedureState previousState) throws ProcedureStateException;

	// Checks for exiting this state e.g. whitelist valid states, conditions, etc
	abstract public void onExitValidation(ProcedureState nextState) throws ProcedureStateException;

	abstract public void onEnter() throws ProcedureStateException;

	abstract public void onExit() throws ProcedureStateException;
}
