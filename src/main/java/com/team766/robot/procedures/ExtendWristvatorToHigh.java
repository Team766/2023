package com.team766.robot.procedures;

import com.team766.robot.mechanisms.Elevator;
import com.team766.robot.mechanisms.Wrist;

public class ExtendWristvatorToHigh extends MoveWristvator {

	public ExtendWristvatorToHigh() {
		super(Elevator.Position.HIGH, Wrist.Position.HIGH_NODE);
	}
}
