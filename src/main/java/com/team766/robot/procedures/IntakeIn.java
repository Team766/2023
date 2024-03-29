package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;

public class IntakeIn extends Procedure{
	public void run(Context context){
		context.takeOwnership(Robot.intake);
		context.takeOwnership(Robot.storage);
		Robot.intake.startIntake();
		Robot.storage.beltIn();
	}
}
