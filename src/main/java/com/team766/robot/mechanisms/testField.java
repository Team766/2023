package com.team766.robot.mechanisms;

import com.team766.robot.mechanisms.Exceptions.JanuaryTagIndexOutOfBoundsException;
import com.team766.robot.mechanisms.Exceptions.valueNotInitializedException;
import java.util.ArrayList;

/**
 * This class represents a test field with various attributes and methods to update robot location.
 */
public class testField {

    /**
     * All lengths are in meters.
     * (0,0) is the bottom left corner.
     * For this field, straight ahead is going to be looking directly at the trash cans (outside S wing).
     */
    final double length = 5.842;
    final double width = 5.7277; 
    final double scoringAreaSize = 0.5461;

    private double robotX;
    private double robotY;

    final static Location scoring1Location = new Location(2.86385, -0.3556);
    final static Location scoring2Location = new Location(2.86385, 6.1976);

    final static ScoringAreaApriltag scoring2 = new ScoringAreaApriltag(scoring1Location, 2, 180);
    final static ScoringAreaApriltag scoring3 = new ScoringAreaApriltag(scoring2Location, 3, 360);

    /**
     * Default constructor for TestField.
     */
    public testField() {
    }

    /**
     * Updates the robot's location based on the given data.
     *
     * @param data The data to update the robot's location with.
     * 
     * This data should be in the form of a combinedCameraData object.
     */
     
    public void updateRobotLocation(combinedCameraData data){
        ArrayList<Location> locations = data.getLocationData();
        ArrayList<Integer> tagIDs = data.getTagIDs();
        ArrayList<Double> headings = data.getHeadingData();

        ArrayList<Double> possibleXpositions = new ArrayList<Double>();
        ArrayList<Double> possibleYpositions = new ArrayList<Double>();

        if(locations.size() != tagIDs.size() || locations.size() != headings.size()){
            throw new valueNotInitializedException("The size of the locations, tagIDs, and headings ArrayLists must be the same.");
        }

        for (int iterations = 0; iterations < locations.size(); iterations++){
            int currentTagID = tagIDs.get(iterations);
            double currentHeading = headings.get(iterations);

            ScoringAreaApriltag tagToUse = findTagCorrespondingToTagID(currentTagID);

            double realTargetOffset = keepSignsSubtraction(currentHeading, tagToUse.getHeading(), currentHeading);

            //do math on real target offset

            
            
            
        }

    }

    private ScoringAreaApriltag findTagCorrespondingToTagID(int tagID){
        if(tagID == scoring2.getTagID()){
            return scoring2;
        } else if(tagID == scoring3.getTagID()){
            return scoring3;
        } else {
            throw new JanuaryTagIndexOutOfBoundsException("The tag ID " + tagID + " does not correspond to any known tag IDs.");
        }
    }

    private double keepSignsSubtraction(double toKeep, double first, double second){
        if(toKeep >= 0){
            if(first - second > 0) return first -second;
            return -1* (first - second);
        }

        if(first - second > 0) return -1 * (first - second);
        return first - second;
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
