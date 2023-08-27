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

        double x2 = arr.get(0);
        double y2 = arr.get(1);
    
        double x3 = arr.get(2);
        double y3 = arr.get(3);
        
        Location robotRelToTag2 = new Location(scoring2.getX() - x2, scoring2.getY() - y2);
        Location robotRelToTag3 = new Location(scoring3.getX() - x3, scoring2.getY() - y3);

        double realX = (robotRelToTag2.getX() + robotRelToTag2.getX()) / 2;
        double realY = (robotRelToTag2.getY() + robotRelToTag3.getY()) / 2;

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
