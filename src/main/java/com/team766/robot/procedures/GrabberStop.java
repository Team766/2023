package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;

public class GrabberStop extends Procedure{
	public void run(Context context){
		context.takeOwnership(Robot.grabber);
		Robot.grabber.grabberStop();
	}
}
