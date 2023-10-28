package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;

public class RetractWristvatorIdleIntake extends Procedure {
	public void run(Context context) {
		new IntakeIdle().run(context);
		new RetractWristvator().run(context);
	}	
}
