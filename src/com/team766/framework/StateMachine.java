package com.team766.framework;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class StateMachine<States extends Enum<States>> implements Command {
	protected final States DONE = null;
	
	public static class Transition {
		private Transition() {
		}
	}
	private static final Transition TRANSITION_MARKER = new Transition();
	
	private HashMap<States, Supplier<Transition>> m_stateFunctions;
	private States m_startState;
	private States m_currentState;
	private BiConsumer<States, States> m_transitionObserver;
	
	public StateMachine() {
		m_stateFunctions = new HashMap<States, Supplier<Transition>>();
		m_startState = DONE;
		m_currentState = DONE;
	}
	
	protected void addState(States state, Supplier<Transition> handler) {
		m_stateFunctions.put(state, handler);
	}
	
	protected <S extends Enum<S>> void addState(States enterState, States exitState, StateMachine<S> submachine) {
		if (submachine.getCurrentState() != submachine.DONE) {
			throw new IllegalStateException("State machines can't be shared");
		}
		submachine.initialize();
		addState(enterState, () -> {
			submachine.run();
			if (submachine.isFinished()) {
				submachine.initialize();
				return changeState(exitState);
			} else {
				return repeatState();
			}
		});
	}
	
	protected Transition changeState(States newState) {
		m_currentState = newState;
		return TRANSITION_MARKER;
	}
	
	protected Transition repeatState() {
		return TRANSITION_MARKER;
	}
	
	protected Transition finish() {
		m_currentState = DONE;
		return TRANSITION_MARKER;
	}
	
	/*
	 * Observer function receives two arguments: previous state and next state.
	 */
	public void setTransitionObserver(BiConsumer<States, States> observer) {
		m_transitionObserver = observer;
	}
	
	protected void setStartState(States startState) {
		m_startState = startState;
	}
	
	public States getCurrentState() {
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
		States prevState = m_currentState;
		Supplier<Transition> stateHandler = m_stateFunctions.get(m_currentState);
		if (stateHandler == null) {
			throw new IllegalStateException("A state function was not added for " + m_currentState);
		}
		stateHandler.get();
		if (m_transitionObserver != null) {
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
