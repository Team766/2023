package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;
import com.team766.robot.mechanisms.Elevator;
import com.team766.robot.mechanisms.Wrist;

public class MoveWristvator extends Procedure {
	private final Elevator.Position elevatorSetpoint;
	private final Wrist.Position wristSetpoint;

	public MoveWristvator(Elevator.Position elevatorSetpoint_, Wrist.Position wristSetpoint_) {
		this.elevatorSetpoint = elevatorSetpoint_;
		this.wristSetpoint = wristSetpoint_;
	}

	@Override
	public final void run(Context context) {
		context.takeOwnership(Robot.wrist);
		context.takeOwnership(Robot.elevator);

		// Always retract the wrist before moving the elevator.
		// It might already be retracted, so it's possible that this step finishes instantaneously.
		Robot.wrist.rotate(Wrist.Position.RETRACTED);
		context.waitFor(() -> Robot.wrist.isNearTo(Wrist.Position.RETRACTED));

		// Move the elevator. Wait until it gets near the target position.
		Robot.elevator.moveTo(elevatorSetpoint);
		context.waitFor(() -> Robot.elevator.isNearTo(elevatorSetpoint));

		// Lastly, move the wrist.
		Robot.wrist.rotate(wristSetpoint);
		context.waitFor(() -> Robot.wrist.isNearTo(wristSetpoint));
	}
}
