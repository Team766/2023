package com.team766.robot.procedures;

import com.team766.robot.mechanisms.Elevator;
import com.team766.robot.mechanisms.Wrist;

public class ExtendWristvatorToLow extends MoveWristvator {

	public ExtendWristvatorToLow() {
		super(Elevator.Position.LOW, Wrist.Position.LEVEL);
	}
}
