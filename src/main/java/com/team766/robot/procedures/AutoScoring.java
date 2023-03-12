package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.odometry.Point;
import com.team766.odometry.PointDir;
import com.team766.robot.procedures.FollowPoints;
import com.team766.robot.Robot;
import com.team766.robot.RobotTargets;

public class AutoScoring extends Procedure{
	Point currentPos;
	double minDistance;
	Point targetPoint;
	Nodes targetNode;

	public enum Nodes {
		HYBRID,
		MEDIUM,
		HIGH
	}

	public AutoScoring(Nodes node) {
		currentPos = Robot.drive.getCurrentPosition().clone();
		minDistance = currentPos.distance(RobotTargets.NODES[0]);
		targetPoint = RobotTargets.NODES[0];
		targetNode = node;

		for (int i = 1; i < RobotTargets.NODES.length; i++) {
			if (currentPos.distance(RobotTargets.NODES[i]) < minDistance) {
				minDistance = currentPos.distance(RobotTargets.NODES[i]);
				targetPoint = RobotTargets.NODES[i];
			}
		}
	}

	public void run(Context context) {
		context.startAsync(new FollowPoints(Robot.drive.getCurrentPosition().clone(), new PointDir[]{new PointDir(targetPoint, 0)}));
		//Do the actual scoring (arm movements, etc.)
	}
}
