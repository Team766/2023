package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import com.team766.robot.Robot;

public class DriveStraight extends Procedure {
	public void run(Context context) {
		context.takeOwnership(Robot.drive);
		Logger.get(Category.PROCEDURES).logRaw(Severity.DEBUG, "Getting started.");
		Robot.drive.setDrivePower(0.25, 0.25);
		context.waitForSeconds(3.0);
		Robot.drive.setDrivePower(0.0, 0.0);
		Logger.get(Category.PROCEDURES).logRaw(Severity.DEBUG, "Done.");
	}
}
