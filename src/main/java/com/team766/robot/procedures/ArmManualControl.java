package com.team766.robot.procedures;

import java.util.function.Supplier;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.logging.Severity;
import com.team766.robot.Robot;

public class ArmManualControl extends Procedure {

	private Supplier<Double> controlValue;

	public ArmManualControl(Supplier<Double> controlValue) {
		this.controlValue = controlValue;
	}

	@Override
	public void run(Context context) {
		log(Severity.WARNING, ">>> State : ArmManualControlState Entered");
		try {
			context.takeOwnership(Robot.arms);
			while (true) {
				// scale degrees per (rough) iteration from -1 thru 1 to ...
				double angularIncrement = controlValue.get() * 10.0d;
				
				double motorAngle = Robot.arms.firstJoint.getMotorPosition();
				Robot.arms.firstJoint.setMotorPosition(motorAngle + angularIncrement);

				context.yield();
			}
		} finally {
			log(Severity.WARNING, ">>> State : ArmManualControlState Exited");
		}
		
	}
	
}
