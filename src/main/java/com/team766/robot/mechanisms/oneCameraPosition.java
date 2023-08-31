package com.team766.robot.mechanisms;

import java.util.ArrayList;

public class oneCameraPosition implements apriltagLocalization{

	ArrayList<Double> arr = new ArrayList<Double>();
	ArrayList<Integer> tags = new ArrayList<Integer>();
	
	public oneCameraPosition(Location tag1, int tagID){
		arr.add(tag1.getX());
		arr.add(tag1.getY());

		tags.add(tagID);
	}
	
	public ArrayList<Double> getData() {
		return arr;
	}

	@Override
	public ArrayList<Integer> getTagIDsInOrder() {
		return tags;
	}
	
}
