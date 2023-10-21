package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;
import com.team766.robot.mechanisms.Wrist;

public class IntakeIdle extends Procedure {
	public void run(Context context) {
		context.takeOwnership(Robot.intake);
		context.takeOwnership(Robot.wrist);

		Robot.intake.idle();
		Robot.wrist.rotate(Wrist.Position.UP);
	}	
}
