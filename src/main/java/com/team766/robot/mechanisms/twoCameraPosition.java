package com.team766.robot.mechanisms;
import java.util.ArrayList;
public class twoCameraPosition implements apriltagLocalization {
	

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
