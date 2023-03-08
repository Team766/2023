package com.team766.robot.procedures;

import java.util.Random;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.logging.Severity;
import com.team766.robot.Robot;

public class ArmAutomatedControl extends Procedure {

	double targetAngle = 0;

	double angleTolerance = 3.0d;

	Random rand = new Random();

	@Override
	public void run(Context context) {
		log(Severity.WARNING, ">>> State : ArmAutomatedControlState Entered");
		try {
			while (true) {
				// moving
				Robot.arms.firstJoint.setMotorPosition(targetAngle);
				context.waitFor(() -> hasReachedTargetAngle());

				// pausing
				context.waitForSeconds(1.000);

				// incrementing
				targetAngle = rand.nextDouble(-25, 45);
			}
		} finally {
			log(Severity.WARNING, ">>> State : ArmAutomatedControlState Exited");
		}
	}

	private boolean hasReachedTargetAngle() {
		double motorAngle = Robot.arms.firstJoint.getMotorPosition();
		if(motorAngle > targetAngle - angleTolerance && motorAngle < targetAngle + angleTolerance) return true;
		return false;

	}

}
