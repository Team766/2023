package com.team766.robot.mechanisms;

import java.util.ArrayList;

public class combinedCameraData {
	
	private ArrayList<Location> locationData;
	private ArrayList<Integer> tagIDs;
	private ArrayList<Double> headingData;

	public combinedCameraData(ArrayList<Location> locationData, ArrayList<Integer> tagIDs, ArrayList<Double> headingData){
		this.locationData = locationData;
		this.tagIDs = tagIDs;
		this.headingData = headingData;
	}

	public ArrayList<Location> getLocationData(){
		return locationData;
	}

	public ArrayList<Integer> getTagIDs(){
		return tagIDs;
	}

	public ArrayList<Double> getHeadingData(){
		return headingData;
	}

	public String toString(){
		return "combinedCameraData object" + super.toString() + "with " + getLocationData().size() + " possible datapoints";
	}
}
