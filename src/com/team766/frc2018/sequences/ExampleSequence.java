package com.team766.frc2018.sequences;

import com.team766.framework.StateMachine;
import com.team766.frc2018.Robot;

public class ExampleSequence extends StateMachine {
	private Robot m_robot;
	
	public ExampleSequence(Robot robot) {
		m_robot = robot;
		m_robot.exampleMechanism.takeControl(this);
		
		setStartState(new MoveForward());
	}
	
	private class MoveForward implements State {
		public State tick() {
			m_robot.exampleMechanism.setTargetForward();
			if (m_robot.exampleMechanism.atTarget()) {
				return new MoveBackward();
			} else {
				return this;
			}
		}
	}
	
	private class MoveBackward implements State {
		public State tick() {
			m_robot.exampleMechanism.setTargetBackward();
			if (m_robot.exampleMechanism.atTarget()) {
				return DONE;
			} else {
				return this;
			}
		}
	}
}