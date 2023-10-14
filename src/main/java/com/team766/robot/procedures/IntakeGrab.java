package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;

public class IntakeGrab extends Procedure{
	public void run(Context context){
		context.takeOwnership(Robot.intake);
		Robot.intake.in();
		context.waitForSeconds(2);
		Robot.intake.stop();
	}
}
