package com.team766.frc2018.sequences;

import com.team766.framework.StateMachine;
import com.team766.frc2018.Robot;

public class ExampleSequence extends StateMachine<ExampleSequence.States> {
	enum States {
		MOVE_FORWARD,
		MOVE_BACKWARD,
	}
	
	public ExampleSequence(Robot robot) {
		setStartState(States.MOVE_FORWARD);
		
		addState(States.MOVE_FORWARD, () -> {
			robot.exampleMechanism.setTargetForward();
			if (robot.exampleMechanism.atTarget()) {
				return changeState(States.MOVE_BACKWARD);
			} else {
				return repeatState();
			}
		});
		
		addState(States.MOVE_BACKWARD, () -> {
			robot.exampleMechanism.setTargetBackward();
			if (robot.exampleMechanism.atTarget()) {
				return finish();
			} else {
				return repeatState();
			}
		});
	}
}