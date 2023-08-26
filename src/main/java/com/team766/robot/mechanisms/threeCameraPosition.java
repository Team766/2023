package com.team766.robot.mechanisms;

import java.util.ArrayList;

public class threeCameraPosition implements apriltagLocalization {

	ArrayList<Double> arr = new ArrayList<Double>();
	ArrayList<Integer> tags = new ArrayList<Integer>();

	public threeCameraPosition(location tag1, location tag2, location tag3, int tag1ID, int tag2ID, int tag3ID){
		arr.add(tag1.getX());
		arr.add(tag1.getY());

		arr.add(tag2.getX());
		arr.add(tag2.getY());
		
		arr.add(tag3.getX());
		arr.add(tag3.getY());

		tags.add(tag1ID);
		tags.add(tag2ID);
		tags.add(tag3ID);
	}

	public ArrayList<Double> getData() {
		return arr;
	}
	
	public ArrayList<Integer> getTagIDsInOrder() {
		return tags;
	}
	
}
