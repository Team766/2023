package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;
import com.team766.robot.mechanisms.Elevator;
import com.team766.robot.mechanisms.Wrist;

public class ExtendWristvatorToLow extends Procedure {

	@Override
	public void run(Context context) {
		context.takeOwnership(Robot.wrist);
		context.takeOwnership(Robot.elevator);

		Robot.elevator.moveTo(Elevator.Position.LOW);
		Robot.wrist.rotate(Wrist.Position.UP);
	}
}
