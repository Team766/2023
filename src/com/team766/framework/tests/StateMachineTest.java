package com.team766.framework.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.team766.framework.Scheduler;
import com.team766.framework.StateMachine;

class StateMachineTest {

	@Test
	void test() {
		TestStateMachine sm = new TestStateMachine();
		sm.initialize();
		
		assertEquals(TestStateMachine.States.START, sm.getCurrentState());
		
		sm.run();
		
		assertEquals(TestStateMachine.States.STATE_1, sm.getCurrentState());		
		
		for (int i = 1; i < 5; ++i) {
			sm.run();
			assertEquals(TestStateMachine.States.STATE_1, sm.getCurrentState());
		}
		
		sm.run();
		
		assertEquals(TestStateMachine.States.STATE_2, sm.getCurrentState());
		
		sm.run();
		
		assertTrue(sm.isFinished());
	}
	
	@Test
	void test2() {
		TestStateMachine2 sm = new TestStateMachine2();
		sm.start();
		
		assertEquals(TestStateMachine2.States.START, sm.getCurrentState());
		
		Scheduler.getInstance().run();
		
		assertEquals(TestStateMachine2.States.STATE_1, sm.getCurrentState());		
		
		for (int i = 1; i < 7; ++i) {
			Scheduler.getInstance().run();
			assertEquals(TestStateMachine2.States.STATE_1, sm.getCurrentState());
		}
		
		Scheduler.getInstance().run();
		
		assertEquals(TestStateMachine2.States.STATE_2, sm.getCurrentState());
		
		Scheduler.getInstance().run();
		
		assertTrue(sm.isFinished());
	}

}

class TestStateMachine extends StateMachine<TestStateMachine.States> {
	enum States {
		START,
		STATE_1,
		STATE_2,
	}
	
	public TestStateMachine() {
		setStartState(States.START);
		addState(States.START, () -> {
			return changeState(States.STATE_1);
		});
		addState(States.STATE_1, new Supplier<Transition>() {
			int count = 0;
			public Transition get() {
				++count;
				if (count < 5) {
					return repeatState();
				} else {
					return changeState(States.STATE_2);
				}
			}
		});
		addState(States.STATE_2, () -> {
			return finish();
		});
	}
}

class TestStateMachine2 extends StateMachine<TestStateMachine2.States> {
	enum States {
		START,
		STATE_1,
		STATE_2,
	}
	
	public TestStateMachine2() {
		setStartState(States.START);
		addState(States.START, () -> {
			return changeState(States.STATE_1);
		});
		addState(States.STATE_1, States.STATE_2, new TestStateMachine());
		addState(States.STATE_2, () -> {
			return finish();
		});
	}
}