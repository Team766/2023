package com.team766.robot.mechanisms;
import java.util.ArrayList;

public class testField{
	/* All lengths in meters 
	 * 
	 * (0,0) is bottom left corner
	*/
	final double length = 5.842;
	final double width = 5.7277; 
	final double scoringAreaSize = 0.5461;

	private double robotX;
	private double robotY;

	final static location scoring1location = new location(2.86385, -0.3556);
	final static location scoring2location = new location(2.86385, 6.1976);

	final static ScoringAreaApriltag scoring2 = new ScoringAreaApriltag(scoring1location, 2);
	final static ScoringAreaApriltag scoring3 = new ScoringAreaApriltag(scoring2location, 3);

	public testField(){

	}

	public void updateRobotLocation(twoCameraPosition t){
		ArrayList<Double> arr = t.getData();

		double x2 = arr.get(0);
		double y2 = arr.get(1);
	
		double x3 = arr.get(2);
		double y3 = arr.get(3);
		


		location robotRelToTag2 = new location(scoring2.getX() - x2, scoring2.getY() - y2);
		location robotRelToTag3 = new location(scoring3.getX() - x3, scoring2.getY() - y3);

		double realX = (robotRelToTag2.getX() + robotRelToTag2.getX()) / 2;
		double realY = (robotRelToTag2.getY() + robotRelToTag3.getY()) / 2;

		robotX = realX;
		robotY = realY;

	}

	public void updateRobotLocation(threeCameraPosition t){
		ArrayList<Double> arr = t.getData();
		
	}

	public ArrayList<Double> logCoords() {
		ArrayList<Double> arr = new ArrayList<Double>();
		arr.add(robotX);
		arr.add(robotY);
		return arr;
	}

}
