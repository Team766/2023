package com.team766.framework;

import com.team766.framework.StateMachine.State;

public abstract class StateSubmachine extends StateMachine.State {

	private StateMachine m_submachine;
	
	public StateSubmachine(StateMachine submachine) {
		if (submachine.getCurrentState() != StateMachine.DONE) {
			throw new IllegalStateException("State machines can't be shared");
		}
		submachine.initialize();
		m_submachine = submachine;
	}

	@Override
	public State tick() {
		m_submachine.run();
		if (m_submachine.isFinished()) {
			m_submachine.initialize();
			return getExitState();
		} else {
			return this;
		}
	}

	protected abstract State getExitState();

}
