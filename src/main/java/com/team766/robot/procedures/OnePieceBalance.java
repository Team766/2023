package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import edu.wpi.first.wpilibj.DriverStation;

public class OnePieceBalance extends Procedure {
	public void run(Context context) {
		new ReverseIntake().run(context);
		new GyroBalance(DriverStation.getAlliance()).run(context);
	}
}
