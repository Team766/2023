package com.team766.robot.procedures;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;

public class CheckPieceRGB extends Procedure {
	public void run(Context context) {
		context.takeOwnership(Robot.cMatcher);
		Robot.cMatcher.makeColorMatches();
		while(true) {
			Robot.cMatcher.checkColor();
			context.waitForSeconds(5.0);
		}
	}
}
