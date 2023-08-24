package com.team766.robot.mechanisms;
import java.util.ArrayList;
public class twoCameraPosition {
	
	private static double X1;
	private static double Y1;
	private static int ID1;
	private static double X2;
	private static double Y2;
	private static int ID2;

	ArrayList<Double> arr = new ArrayList<Double>();
	public twoCameraPosition(location tag1, location tag2){
		arr.add(tag1.getX());
		arr.add(tag1.getY());
		
		arr.add(tag2.getX());
		arr.add(tag2.getY());

	}

	public ArrayList<Double> getData(){
		return arr;
	}


}
