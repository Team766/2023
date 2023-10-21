package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;
import com.team766.robot.mechanisms.Intake.GamePieceType;

public class GoForCones extends Procedure {

	@Override
	public void run(Context context) {
		context.takeOwnership(Robot.intake);
		// TODO: grab ownership of the Lights, as well

		Robot.intake.setGamePieceType(GamePieceType.CONE);
		// TODO: turn lights to cone color
	}
}
