package com.team766.robot.procedures;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;

public class SolenoidTest extends Procedure {
	
	public void run(Context context) {
		context.takeOwnership(Robot.testSolenoid);
		log("Output Before: " + Robot.testSolenoid.getAnalog());
		Robot.testSolenoid.setPusher(true);
		context.waitForSeconds(2);
		Robot.testSolenoid.setPusher(false);
		log("Output After: " + Robot.testSolenoid.getAnalog());
	}
}
