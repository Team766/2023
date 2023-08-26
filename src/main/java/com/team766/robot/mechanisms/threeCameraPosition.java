package com.team766.robot.mechanisms;

import java.util.ArrayList;

public class threeCameraPosition implements apriltagLocalization {

	ArrayList<Double> arr = new ArrayList<Double>();

	public threeCameraPosition(location tag1, location tag2, location tag3){
		arr.add(tag1.getX());
		arr.add(tag1.getY());

		arr.add(tag2.getX());
		arr.add(tag2.getY());
		
		arr.add(tag3.getX());
		arr.add(tag3.getY());
	}
	public ArrayList<Double> getData() {
		return arr;
	}
	
}
