package com.team766.robot.mechanisms;

public class location {
	
	/* Class to store a location in X Y coords of something
	 * This could be the location of the scoring area on the field or something like that
	 * This class was designed to have no setter methods, as this class should be used for something on the field that doesn't move
	 */
	private double x;
	private double y;

	public location(double X, double Y){
		x = X;
		y = Y;
	}

	public double getX(){
		return x;
	}

	public double getY(){
		return y;
	}
}
