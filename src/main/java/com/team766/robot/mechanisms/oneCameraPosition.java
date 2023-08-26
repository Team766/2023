package com.team766.robot.mechanisms;

import java.util.ArrayList;

public class oneCameraPosition implements apriltagLocalization{

	ArrayList<Double> arr = new ArrayList<Double>();

	public oneCameraPosition(location tag1){
		arr.add(tag1.getX());
		arr.add(tag1.getY());
	}
	
	public ArrayList<Double> getData() {
		return arr;
	}
	
}
