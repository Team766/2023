package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.logging.Category;

public class DriveSquare extends Procedure {
	public DriveSquare(){
		loggerCategory=Category.AUTONOMOUS;
	}
	public void run(Context context){
		for(int i=0;i<4;i++){
			new DriveStraight().run(context);
			new TurnRight().run(context);
			log("Side "+i+" complete");
		}
	}
}
