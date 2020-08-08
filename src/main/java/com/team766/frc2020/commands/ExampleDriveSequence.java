package com.team766.frc2020.commands;

import com.team766.framework.Subroutine;
import com.team766.frc2020.Robot;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public class ExampleDriveSequence extends Subroutine {
	public ExampleDriveSequence() {
		takeControl(Robot.drive);
	}
	
	protected void subroutine() {
		Logger.get(Category.AUTONOMOUS).log(Severity.INFO, "Forward movement begins");
		double forwardStartTime = RobotProvider.instance.getClock().getTime();
		Robot.drive.setDrivePower(0.95, 1.0);
		waitFor(() -> RobotProvider.instance.getClock().getTime() - forwardStartTime > 1.0);
		Logger.get(Category.AUTONOMOUS).log(Severity.INFO, "Forward movement finished");
		
		Logger.get(Category.AUTONOMOUS).log(Severity.INFO, "Reverse movement begins");
		double reverseStartTime = RobotProvider.instance.getClock().getTime();
		Robot.drive.setDrivePower(-1.0, -0.95);
		waitFor(() -> RobotProvider.instance.getClock().getTime() - reverseStartTime > 1.0);
		Logger.get(Category.AUTONOMOUS).log(Severity.INFO, "Reverse movement finished");
		
		Robot.drive.setDrivePower(0.0, 0.0);
	}
}
