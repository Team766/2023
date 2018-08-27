package com.team766.framework;

import java.util.function.BiConsumer;

public class StateMachine implements Command {
	public interface State {
		public State tick();
	}

	protected static final State DONE = null;
	
	private State m_startState;
	private State m_currentState;
	private BiConsumer<State, State> m_transitionObserver;
	
	public StateMachine() {
		m_startState = DONE;
		m_currentState = DONE;
	}
	
	/*
	 * Observer function receives two arguments: previous state and next state.
	 */
	public void setTransitionObserver(BiConsumer<State, State> observer) {
		m_transitionObserver = observer;
	}
	
	protected void setStartState(State startState) {
		m_startState = startState;
	}
	
	public State getCurrentState() {
		return m_currentState;
	}
	
	public void initialize() {
		if (m_startState == DONE) {
			throw new IllegalStateException("Remember to call setStartState when setting up your state machine");
		}
		m_currentState = m_startState;
	}
	
	public void run() {
		if (isFinished()) {
			return;
		}
		State prevState = m_currentState;
		m_currentState = m_currentState.tick();
		if (m_transitionObserver != null && prevState != m_currentState) {
			m_transitionObserver.accept(prevState, m_currentState);
		}
	}
	
	public void start() {
		initialize();
		Scheduler.getInstance().add(this);
	}
	
	public void stop() {
		Scheduler.getInstance().cancel(this);
	}
	
	public boolean isFinished() {
		return m_currentState == DONE;
	}
}
