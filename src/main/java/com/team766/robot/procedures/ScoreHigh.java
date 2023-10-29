package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;
import com.team766.robot.mechanisms.Intake.GamePieceType;

public class ScoreHigh extends Procedure {
	private final GamePieceType type;

	public ScoreHigh(GamePieceType type) {
		this.type = type;
	}
	public void run(Context context) {
		context.takeOwnership(Robot.intake);
		Robot.intake.setGamePieceType(type);
		context.releaseOwnership(Robot.intake);
		new ExtendWristvatorToHigh().run(context);
		new IntakeOut().run(context);
		context.waitForSeconds(1);
		new IntakeStop().run(context);
	}
}