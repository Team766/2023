package com.team766.framework.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.team766.framework.Scheduler;
import com.team766.framework.StateMachine;
import com.team766.framework.StateSubmachine;

class StateMachineTest {

	@Test
	void test() {
		TestStateMachine sm = new TestStateMachine();
		sm.initialize();
		
		assertTrue(sm.getCurrentState() instanceof TestStateMachine.StartState);
		
		sm.run();
		
		assertTrue(sm.getCurrentState() instanceof TestStateMachine.State1);
		
		for (int i = 1; i < 5; ++i) {
			sm.run();
			assertTrue(sm.getCurrentState() instanceof TestStateMachine.State1);
		}
		
		sm.run();
		
		assertTrue(sm.getCurrentState() instanceof TestStateMachine.State2);
		
		sm.run();
		
		assertTrue(sm.isFinished());
	}
	
	@Test
	void test2() {
		TestStateMachine2 sm = new TestStateMachine2();
		sm.start();
		
		assertTrue(sm.getCurrentState() instanceof TestStateMachine2.StartState);
		
		Scheduler.getInstance().run();
		
		assertTrue(sm.getCurrentState() instanceof TestStateMachine2.State1);
		
		for (int i = 1; i < 7; ++i) {
			Scheduler.getInstance().run();
			assertTrue(sm.getCurrentState() instanceof TestStateMachine2.State1);
		}
		
		Scheduler.getInstance().run();
		
		assertTrue(sm.getCurrentState() instanceof TestStateMachine2.State2);
		
		Scheduler.getInstance().run();
		
		assertTrue(sm.isFinished());
	}

}

class TestStateMachine extends StateMachine {
	class StartState implements State {
		public State tick() {
			return new State1();
		}
	}
	
	class State1 implements State {
		int count = 0;
		public State tick() {
			++count;
			if (count < 5) {
				return this;
			} else {
				return new State2();
			}
		}
	}
	
	class State2 implements State {
		public State tick() {
			return DONE;
		}
	}

	public TestStateMachine() {
		setStartState(new StartState());
	}
}

class TestStateMachine2 extends StateMachine {
	class StartState implements State {
		public State tick() {
			return new State1();
		}
	}
	
	class State1 extends StateSubmachine {
		public State1() {
			super(new TestStateMachine());
		}
		
		public State getExitState() {
			return new State2();
		}
	}
	
	class State2 implements State {
		public State tick() {
			return DONE;
		}
	}
	
	public TestStateMachine2() {
		setStartState(new StartState());
	}
}