package com.team766.frc2018.sequences;

import com.team766.framework.StateMachine;
import com.team766.frc2018.Robot;

public class ExampleSequence extends StateMachine {
	private Robot m_robot;
	
	public ExampleSequence(Robot robot) {
		m_robot = robot;
		m_robot.arm.takeControl(this);
		
		setStartState(new MoveUp());
	}
	
	private class MoveUp extends State {
		public State tick() {
			m_robot.arm.setTargetUp();
			if (m_robot.arm.atTarget()) {
				return new MoveDown();
			} else {
				return this;
			}
		}
	}
	
	private class MoveDown extends State {
		public State tick() {
			m_robot.arm.setTargetDown();
			if (m_robot.arm.atTarget()) {
				return DONE;
			} else {
				return this;
			}
		}
	}
}