package com.team766.robot.mechanisms;

public class ScoringAreaApriltag {
	
	private int ID;

	private double x;
	private double y;
	public ScoringAreaApriltag(location l, int ID){
		x = l.getX();
		y = l.getY();
	}

	public double getX(){
		return x;
	}

	public double getY(){
		return y;
	}

	public int getTagID(){
		return ID;
	}
}
