package com.team766.robot.mechanisms;

import com.team766.robot.mechanisms.Exceptions.valueNotInitializedException;

public class ScoringAreaApriltag {
	
	private int ID;

	private double x;
	private double y;
	private double heading = -1;

	public ScoringAreaApriltag(Location l, int ID){
		x = l.getX();
		y = l.getY();
	}

	// override, will change it to only this constructor later but dont want to break everything.
	public ScoringAreaApriltag(Location l, int ID, double heading){
		this(l, ID);
		this.heading = heading;
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

	public double getHeading(){
		if(heading < 0){
			throw new valueNotInitializedException("Heading was not initialized for AprilTag with tag ID " + getTagID() + " at field X position " + getX() + " and field Y position " + getY());
		}
		return heading;
	}
	
}
