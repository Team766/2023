package com.team766.robot.procedures;

import com.team766.robot.mechanisms.Elevator;
import com.team766.robot.mechanisms.Wrist;
import com.team766.robot.mechanisms.Intake.GamePieceType;

public class ExtendWristvatorToHuman extends MoveWristvator {

	public ExtendWristvatorToHuman(GamePieceType gamePieceType) {
		super(gamePieceType == GamePieceType.CONE
				? Elevator.Position.HUMAN_CONES
				: Elevator.Position.HUMAN_CUBES,
			Wrist.Position.LEVEL);
	}
}
