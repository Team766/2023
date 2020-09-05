package com.team766.frc2020.commands;

import com.team766.framework.Command;
import com.team766.framework.Context;
import com.team766.frc2020.Robot;

public class Launch extends Command {
	public void run(Context context) {
        context.takeControl(Robot.launcher);

        log("Launching ball");
        Robot.launcher.setPlunger(true);
        context.waitForSeconds(0.5);
        Robot.launcher.setPlunger(false);
    }
}
