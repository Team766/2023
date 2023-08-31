package com.team766.robot.mechanisms;

import java.util.ArrayList;

/**
 * This class represents a test field with various attributes and methods to update robot location.
 */
public class TestField {

    /**
     * All lengths are in meters.
     * (0,0) is the bottom left corner.
     */
    final double length = 5.842;
    final double width = 5.7277; 
    final double scoringAreaSize = 0.5461;

    private double robotX;
    private double robotY;

    final static Location scoring1Location = new Location(2.86385, -0.3556);
    final static Location scoring2Location = new Location(2.86385, 6.1976);

    final static ScoringAreaApriltag scoring2 = new ScoringAreaApriltag(scoring1Location, 2);
    final static ScoringAreaApriltag scoring3 = new ScoringAreaApriltag(scoring2Location, 3);

    /**
     * Default constructor for TestField.
     */
    public TestField() {
    }

    /**
     * Updates the robot's location using a twoCameraPosition object.
     *
     * @param t A twoCameraPosition object containing the necessary data.
     */
    public void updateRobotLocation(twoCameraPosition t) {
        ArrayList<Double> arr = t.getData();
        ArrayList<Integer> tagIDs = t.getTagIDsInOrder();
        ArrayList<Location> locationList = new ArrayList<Location>();

        int incremental = 0;
        
        for(int tagID : tagIDs){
            if(tagID == scoring2.getTagID()){
                double x = arr.get(incremental);
                incremental++;
                double y = arr.get(incremantal);
                incremental++;

                locationList.add(new Location(scoring2.getX() - x, scoring2.getY() - y));
            }else if(tagID == scoring3.getTagID()){
                double x = arr.get(incremental);
                incremental++;
                double y = arr.get(incremental);
                incremental++;

                locationList.add(new Location(scoring3.getX() - x, scoring3.getY() - y));
            }else{
                throw new LocalizationException("Shoot... The array of tagIDs didn't have this one, tag ID: " + tagID + ", in it");
            }
        }



        Location loc1 = locationList.get(0);
        Location loc2 = locationList.get(1);

        locationList.clear();

        double realX = (loc1.getX() + loc2.getX()) / 2;
        double realY = (loc1.getY() + loc2.getY()) / 2;

        robotX = realX;
        robotY = realY;
    }

    /**
     * Updates the robot's location using a threeCameraPosition object.
     *
     * @param t A threeCameraPosition object containing the necessary data.
     */
    public void updateRobotLocation(threeCameraPosition t) {
        ArrayList<Double> arr = t.getData();
        ArrayList<Integer> tagIDs = t.getTagIDsInOrder();

        // TODO: Implement the update logic using the provided data.
    }

    /**
     * Updates the robot's location using a oneCameraPosition object.
     *
     * @param t A oneCameraPosition object containing the necessary data.
     */
    public void updateRobotLocation(oneCameraPosition t) {
        ArrayList<Double> arr = t.getData();
        ArrayList<Integer> tagIDs = t.getTagIDsInOrder();

        // TODO: Implement the update logic using the provided data.
    }

    /**
     * Logs the current robot coordinates.
     *
     * @return An ArrayList containing the robot's X and Y coordinates.
     */
    public ArrayList<Double> logCoords() {
        ArrayList<Double> arr = new ArrayList<Double>();
        arr.add(robotX);
        arr.add(robotY);
        return arr;
    }
}
