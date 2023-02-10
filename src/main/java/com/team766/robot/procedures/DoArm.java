package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;

public class DoArm extends Procedure {

	@Override
	public void run(Context context) {
		context.takeOwnership(Robot.arm);
		Robot.arm.setMotorPower(-0.06, 0.15);
	}

}
