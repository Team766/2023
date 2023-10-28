package com.team766.robot.procedures;

import com.team766.robot.mechanisms.Elevator;
import com.team766.robot.mechanisms.Wrist;

public class ExtendWristvatorToMid extends MoveWristvator {

	public ExtendWristvatorToMid() {
		super(Elevator.Position.MID, Wrist.Position.MID_NODE);
	}
}
