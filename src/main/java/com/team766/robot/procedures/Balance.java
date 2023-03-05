package com.team766.robot.procedures;

import com.kauailabs.navx.frc.AHRS;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import com.team766.robot.Robot;
import edu.wpi.first.wpilibj.I2C;

public class Balance extends Procedure {
	double default_power = 0.15;

	public void run(Context context) {
		AHRS gyro = new AHRS(I2C.Port.kOnboard);
		while (!gyro.isConnected()) {	
			context.waitForSeconds(0.1);
			Logger.get(Category.PROCEDURES).logRaw(Severity.DEBUG, "Waiting for gyro.");
		}
		gyro.reset();
		context.takeOwnership(Robot.drive);
		//gyro.enableLogging(true);
		//Logger.get(Category.PROCEDURES).logRaw(Severity.DEBUG, "Resetting");
		//Logger.get(Category.PROCEDURES).logRaw(Severity.DEBUG, "Calibrating");
		//Logger.get(Category.PROCEDURES).logRaw(Severity.DEBUG, "Done");
		while (true) {	
			Logger.get(Category.PROCEDURES).logRaw(Severity.DEBUG, "Roll: " + gyro.getRoll() + " Pitch: " + gyro.getPitch());
			context.waitForSeconds(0.01);
			double pitch = gyro.getPitch();
			double power = 0;
			if (pitch > 5) {
				power = -default_power;
			} else if (pitch < -5) {
				power = default_power;
			}
			Robot.drive.setDrivePower(power, power);
		}
	}	
}
