package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;

public class IntakeRelease extends Procedure{
	public void run(Context context){
		context.takeOwnership(Robot.intake);
		Robot.intake.out();
		context.waitForSeconds(2);
		Robot.intake.stop();
	}
}