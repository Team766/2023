package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;

public class ReverseIntake extends Procedure{
	public void run(Context context) {
		context.startAsync(new IntakeOut());
		context.waitForSeconds(2);
		context.startAsync(new IntakeStop());
	}
}
