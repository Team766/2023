package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;

public class WaitFiveSeconds extends Procedure {
	public void run(Context context) {
		context.waitForSeconds(5);
	}
}
