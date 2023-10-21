package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;
import com.team766.robot.mechanisms.Elevator;
import com.team766.robot.mechanisms.Wrist;
import com.team766.robot.mechanisms.Intake.GamePieceType;

public class ExtendWristvatorToHuman extends Procedure {

	@Override
	public void run(Context context) {
		context.takeOwnership(Robot.intake);
		context.takeOwnership(Robot.wrist);
		context.takeOwnership(Robot.elevator);

		if (Robot.intake.getGamePieceType() == GamePieceType.CONE) {
			Robot.elevator.moveTo(Elevator.Position.HUMAN_CONES);
			Robot.wrist.rotate(Wrist.Position.LEVEL);
		} else {
			Robot.elevator.moveTo(Elevator.Position.HUMAN_CUBES);
			Robot.wrist.rotate(Wrist.Position.LEVEL);
		}
	}
}
