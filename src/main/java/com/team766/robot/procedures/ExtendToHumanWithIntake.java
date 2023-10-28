package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;
import com.team766.robot.mechanisms.Intake.GamePieceType;

public class ExtendToHumanWithIntake extends Procedure {
	private final GamePieceType gamePieceType;
	public ExtendToHumanWithIntake(GamePieceType gamePieceType) {
		this.gamePieceType = gamePieceType;
	}

	public void run(Context context) {
		context.takeOwnership(Robot.intake);
		context.takeOwnership(Robot.wrist);
		context.takeOwnership(Robot.elevator);

		new IntakeIn().run(context);
		new ExtendWristvatorToHuman(gamePieceType).run(context);
	}
	
}
