package com.team766.robot.mechanisms;
import java.util.ArrayList;
public class twoCameraPosition implements apriltagLocalization {
	
	ArrayList<Double> arr = new ArrayList<Double>();
	ArrayList<Integer> tags = new ArrayList<Integer>();

	public twoCameraPosition(Location tag1, Location tag2, int tag1ID, int tag2ID){
		arr.add(tag1.getX());
		arr.add(tag1.getY());
		
		arr.add(tag2.getX());
		arr.add(tag2.getY());

		tags.add(tag1ID);
		tags.add(tag2ID);
	}

	public ArrayList<Double> getData(){
		return arr;
	}

	
	public ArrayList<Integer> getTagIDsInOrder() {
		// TODO Auto-generated method stub
		return tags;
	}

}
