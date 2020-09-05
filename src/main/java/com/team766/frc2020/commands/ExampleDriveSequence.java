package com.team766.frc2020.commands;

import com.team766.framework.Command;
import com.team766.framework.Context;
import com.team766.frc2020.Robot;
import com.team766.logging.Category;

public class ExampleDriveSequence extends Command {
    public ExampleDriveSequence() {
        loggerCategory = Category.AUTONOMOUS;
    }

	public void run(Context context) {
        context.takeControl(Robot.drive);
        context.takeControl(Robot.intake);

        Robot.intake.setIntakeArm(false);
        Robot.intake.setIntakePower(1.0);

        for (int i = 0; i < 4; ++i) {
            log("Forward movement begins");
            Robot.drive.setDrivePower(0.3, 0.3);
            context.waitForSeconds(2.3);
            log("Forward movement finished");
            
            log("Turning movement begins");
            Robot.drive.setDrivePower(0.1, -0.1);
            context.waitForSeconds(2.3);
            log("Turning movement finished");
        }
            
		Robot.drive.setDrivePower(0.0, 0.0);
	}
}
