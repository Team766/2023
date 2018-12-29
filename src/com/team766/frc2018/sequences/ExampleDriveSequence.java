package com.team766.frc2018.sequences;

import com.team766.framework.StateMachine;
import com.team766.frc2018.Robot;
import com.team766.hal.RobotProvider;

public class ExampleDriveSequence extends StateMachine {
	private Robot m_robot;
	
	public ExampleDriveSequence(Robot robot) {
		m_robot = robot;
		m_robot.arm.takeControl(this);
		
		setStartState(new MoveForward());
	}
	
	private class MoveForward extends State {
		private double startTime;
		
		public void initialize() {
			startTime = RobotProvider.instance.getClock().getTime();
		}
		
		public State tick() {
			m_robot.drive.setDrivePower(0.95, 1.0);
			if (RobotProvider.instance.getClock().getTime() - startTime > 1.0) {
				return new MoveBackward();
			} else {
				return this;
			}
		}
	}
	
	private class MoveBackward extends State {
		private double startTime;
		
		public void initialize() {
			startTime = RobotProvider.instance.getClock().getTime();
		}
		
		public State tick() {
			m_robot.drive.setDrivePower(-1.0, -0.95);
			if (RobotProvider.instance.getClock().getTime() - startTime > 1.0) {
				return new Stop();
			} else {
				return this;
			}
		}
	}
	
	private class Stop extends State {
		public State tick() {
			m_robot.drive.setDrivePower(0.0, 0.0);
			return DONE;
		}
	}
}