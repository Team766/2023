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
	Piece targetPiece;

	public enum Nodes {
		HYBRID,
		MEDIUM,
		HIGH
	}

	public enum Piece {
		HYBRID,
		CUBE,
		CONE
	}

	public AutoScoring(Nodes node) {
		this(node, Piece.HYBRID);

		if (node == Nodes.HYBRID) {
			targetPiece = Piece.HYBRID;
		} else {
			targetPiece = Piece.CONE;
			for (int i = 0; i < RobotTargets.CUBE_ROWS.length; i++) {
				if (targetPoint.getY() == RobotTargets.CUBE_ROWS[i]) {
					targetPiece = Piece.CUBE;
				}
			}
		}
	}

	public AutoScoring(Nodes node, Piece piece) {

		Point[] pointList;
		switch (piece) {
			case CUBE: pointList = RobotTargets.CUBE_NODES;
			case CONE: pointList = RobotTargets.CONE_NODES;
			default: pointList = RobotTargets.NODES;
		}

		targetPiece = piece;

		currentPos = Robot.drive.getCurrentPosition().clone();
		minDistance = currentPos.distance(RobotTargets.NODES[0]);
		targetPoint = RobotTargets.NODES[0];
		targetNode = node;

		for (int i = 1; i < pointList.length; i++) {
			if (currentPos.distance(pointList[i]) < minDistance) {
				minDistance = currentPos.distance(RobotTargets.NODES[i]);
				targetPoint = RobotTargets.NODES[i];
			}
		}
	}

	public void run(Context context) {
		log("Starting AutoScoring");
		context.startAsync(new FollowPoints(Robot.drive.getCurrentPosition().clone(), new PointDir[]{new PointDir(targetPoint, 0)}));
		//Do the actual scoring (arm movements, etc.)
	}
}
