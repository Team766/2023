package com.team766.robot.states;

import java.util.Random;
import com.ctre.phoenix.time.StopWatch;
import com.team766.library.fsm.FiniteState;
import com.team766.logging.Severity;
import com.team766.robot.Robot;

public class ArmAutomatedControlState extends FiniteState {

	StopWatch movementStopwatch = new StopWatch();

	double targetAngle = 0;

	double angleTolerance = 3.0d;

	int movementState = 0;

	Random rand = new Random();

	public ArmAutomatedControlState() {
		super();
		//TODO Auto-generated constructor stub
	}

	@Override
	public void onEnterValidation(FiniteState previousState) throws Exception {
		// allow enter from null (initial) state
		if(previousState == null) return;
		if(previousState instanceof ArmManualControlState) return;
		throw new Exception("Invalid enter transition from previous state");
	}

	@Override
	public void onExitValidation(FiniteState nextState) throws Exception {
		if(nextState instanceof ArmManualControlState) return;
		throw new Exception("Invalid transition to next state");
	}

	@Override
	public void onEnter() throws Exception {
		logger.logRaw(Severity.WARNING, ">>> State : ArmAutomatedControlState Entered");
	}

	@Override
	public void onExit() throws Exception {
		logger.logRaw(Severity.WARNING, ">>> State : ArmAutomatedControlState Exited");
	}

	@Override
	public void run() throws Exception {
		switch(movementState) {
			case 0:
				// moving
				if(!hasReachedTargetAngle()) {
					Robot.arms.firstJoint.setMotorPosition(targetAngle);
				} else {
					movementStopwatch.start();
					movementState++;
				}

				break;

			case 1:
				// pausing
				if(movementStopwatch.getDurationMs() > 1000) {
					movementState++;
				}
				break;

			case 2:
				// incrementing
				targetAngle = rand.nextDouble(-25, 45);
				movementState = 0;
				break;
		}
	}

	private boolean hasReachedTargetAngle() {
		double motorAngle = Robot.arms.firstJoint.getMotorPosition();
		if(motorAngle > targetAngle - angleTolerance && motorAngle < targetAngle + angleTolerance) return true;
		return false;
	
	}
	
}
