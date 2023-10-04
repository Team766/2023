package com.team766.robot.mechanisms;

import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.team766.config.ConfigFileReader;
import com.team766.controllers.PIDController;
import com.team766.framework.Context;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.library.RateLimiter;
import com.team766.library.ValueProvider;
import com.team766.logging.Category;
import com.team766.robot.mechanisms.Exceptions.apriltagHasTargetsCheckedException;
import com.team766.robot.mechanisms.Exceptions.januaryTagException;
//import com.team766.logging.Category;
import de.erichseifert.gral.util.GeometryUtils;
import de.erichseifert.gral.util.MathUtils;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.math.util.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.tools.ForwardingFileObject;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

public class januaryTag extends Mechanism{
	private PhotonCamera camera1;
    private PhotonCamera camera2;
    private PhotonCamera camera3;
    private double deadzoneX;
    private double deadzoneY;
    private double x_target;
    private double y_target;
    private MotorController leftMotor;
    private MotorController rightMotor;
    private double x_targetConstant;
    private double turnConstant;
    private double forwardConstant;
    private double offsetX;
    private double offsetY;
    private Transform3d offset;
    
    private testField field = new testField();


    private PIDController motionX;
    private PIDController motionY;
	public januaryTag(){

        motionX = new PIDController(0.0,0.0,0.0,-0.2,0.2,0.5);
        motionY = new PIDController(0,0,0,-0.2,0.2,1);
        
        loggerCategory = Category.MECHANISMS;
		camera1 = new PhotonCamera("PhotonCamera1");
        camera2 = new PhotonCamera("PhotonCamera2");
        camera3 = new PhotonCamera("PhotonCamera3");
        leftMotor = RobotProvider.instance.getMotor("leftMotor");
        rightMotor = RobotProvider.instance.getMotor("rightMotor");

        //Sample values, we don't know these work at all yet
        x_targetConstant = 0.5;
        turnConstant = 0.3;
        forwardConstant = 0.2;
        offsetX = 0.75;
        offsetY = 0;
        offset = new Transform3d(new Translation3d(-offsetX, -offsetY, 0), new Rotation3d());
        checkContextOwnership();

        motionX.setP(0.01);
        motionY.setP(0.01);

	}
    //Manually move the robot
    public void manual(double left, double right){
        leftMotor.set(left);
        rightMotor.set(right);
    }

    public Transform3d getBestTag() throws apriltagHasTargetsCheckedException{
        return getBestCameraToTarget(getBestTrackedTarget());
    }

    //Test
    public void testLocalization(){

    }
    public void swerveCalculate() throws apriltagHasTargetsCheckedException{
        Transform3d targetTransform = getBestCameraToTarget(getBestTrackedTarget());
        
        double xCurPos = targetTransform.getX();
        double yCurPos = targetTransform.getY();

        motionX.setSetpoint(0);
        motionY.setSetpoint(0);

        motionX.calculate(xCurPos);
        motionY.calculate(yCurPos);

        double xSpeed = motionX.getOutput();
        double ySpeed = motionY.getOutput();

        

    }


    /*
     * This method returns the best target that the camera is currently tracking
     * This is useful for the transform3d data
     * @return PhotonTrackedTarget - the best target that the camera is currently tracking
     * @throws apriltagHasTargetsCheckedException  - Checked exception if there is no targets
     */
    public PhotonTrackedTarget getBestTrackedTarget() throws apriltagHasTargetsCheckedException{
        var result = camera1.getLatestResult(); //getting the result from the camera
        boolean hasTargets = result.hasTargets(); // checking to see if there are any targets in the camera's view. IF THERE ISN'T AND YOU USE result.getTargets() YOU WILL GET AN ERROR

        if(hasTargets){
            List<PhotonTrackedTarget> targets = result.getTargets(); // getting targets
            
            PhotonTrackedTarget bestTrackedTarget = result.getBestTarget(); // getting the best target that is currently being picked up by the camera so that it can know where it is
            return bestTrackedTarget;
        }else{
            log("No targets? see what i did there");
            throw new apriltagHasTargetsCheckedException("There were no targets that could be picked up by the camera, so I'm gonna have to throw this error here.");
        }
    }

    public PhotonTrackedTarget getBestTrackedTargetForCameraTwo() throws apriltagHasTargetsCheckedException{
        var result = camera2.getLatestResult(); //getting the result from the camera
        boolean hasTargets = result.hasTargets(); // checking to see if there are any targets in the camera's view. IF THERE ISN'T AND YOU USE result.getTargets() YOU WILL GET AN ERROR

        if(hasTargets){
            List<PhotonTrackedTarget> targets = result.getTargets(); // getting targets
            
            PhotonTrackedTarget bestTrackedTarget = result.getBestTarget(); // getting the best target that is currently being picked up by the camera so that it can know where it is
            return bestTrackedTarget;
        }else{
            log("No targets? see what i did there");
            throw new apriltagHasTargetsCheckedException("There were no targets that could be picked up by the camera, so I'm gonna have to throw this error here.");
        }
    }

    public PhotonTrackedTarget getBestTrackedTargetForCameraThree() throws apriltagHasTargetsCheckedException{
        var result = camera3.getLatestResult(); //getting the result from the camera
        boolean hasTargets = result.hasTargets(); // checking to see if there are any targets in the camera's view. IF THERE ISN'T AND YOU USE result.getTargets() YOU WILL GET AN ERROR

        if(hasTargets){
            List<PhotonTrackedTarget> targets = result.getTargets(); // getting targets
            
            PhotonTrackedTarget bestTrackedTarget = result.getBestTarget(); // getting the best target that is currently being picked up by the camera so that it can know where it is
            return bestTrackedTarget;
        }else{
            log("No targets? see what i did there");
            throw new apriltagHasTargetsCheckedException("There were no targets that could be picked up by the camera, so I'm gonna have to throw this error here.");
        }
    }


    public void setXtargetConstant(double constant){
        x_targetConstant = constant;
    }

    /*
     * For all next three methods, the constants are the constants that are used in the arcade drive method
     * You can set them individually or together
     */

    public void setTurnConstant(double constant){
        turnConstant = constant;
    }

    public void setForwardConstant(double constant){
        forwardConstant = constant;
    }

    public void setArcadeDriveVisionConstants(double turn, double forward){
        turnConstant = turn;
        forwardConstant = forward;
    }

    /*
     * This method returns the Transform3d data from the best target that the camera is currently tracking
     * @param target - the target that you want the camera to get the data from (use getBestTrackedTarget() to get the best target that the camera is currently tracking)
     * @return Transform3d - the Transform3d from the best target that the camera is currently tracking
     */

    public Transform3d getBestCameraToTarget(PhotonTrackedTarget target){
        return target.getBestCameraToTarget();
    }

    /*
     * This method returns the data from the best target that the camera is currently tracking, not the Transform3d data
     * @return ArrayList<Double> - the data from the best target that the camera is currently tracking
     * [0] is the yaw
     * [1] is the pitch
     * [2] is the area that the apriltag fills the camera view with
     * [3] is the fiducial id of the april tag
     */
    public ArrayList<Double>  getPhotonTrackedTargetData() throws apriltagHasTargetsCheckedException{
        ArrayList<Double> arr = new ArrayList<Double>();
        PhotonTrackedTarget theTarget = getBestTrackedTarget();
        arr.add(theTarget.getYaw());
        arr.add(theTarget.getPitch());
        arr.add(theTarget.getArea());
        arr.add((double) theTarget.getFiducialId());
        return arr;
    }

    public void doSensorFusion(){

        ArrayList<PhotonTrackedTarget> compatableCameraTargets;
        ArrayList<Location> sightTargetLocations = new ArrayList<Location>();
        ArrayList<Integer> sightTargetIDs = new ArrayList<Integer>();
        ArrayList<Double> sightTargetHeadings = new ArrayList<Double>();

        
        
        try{
            compatableCameraTargets = getPhotonTrackedTargetsThatWillWork();
        } catch (apriltagHasTargetsCheckedException e){
            return;
        }

        int numberOfCameras = compatableCameraTargets.size();

        for (PhotonTrackedTarget photonTrackedTarget : compatableCameraTargets){
            int targetID = photonTrackedTarget.getFiducialId();
            sightTargetIDs.add(targetID);
            Transform3d targetTransform = getBestCameraToTarget(photonTrackedTarget);
            //Transform3d scoring = targetTransform.plus(offset);
            double x = targetTransform.getX();
            double y = targetTransform.getY();
            sightTargetLocations.add(new Location(x, y));
            double heading = targetTransform.getRotation().getZ();
            sightTargetHeadings.add(heading);
        }
        
        field.updateRobotLocation(new combinedCameraData(sightTargetLocations, sightTargetIDs, sightTargetHeadings));
    }

    private ArrayList<PhotonTrackedTarget> getPhotonTrackedTargetsThatWillWork() throws apriltagHasTargetsCheckedException{
        ArrayList<PhotonTrackedTarget> compatableCameraTargets = new ArrayList<PhotonTrackedTarget>();
        try {
            PhotonTrackedTarget camera1PhotonTrackedTarget = getBestTrackedTarget();
            compatableCameraTargets.add(camera1PhotonTrackedTarget);
        } catch (apriltagHasTargetsCheckedException e) {
            // no targets
        }

        try {
            PhotonTrackedTarget camera2PhotonTrackedTarget = getBestTrackedTargetForCameraTwo();
            compatableCameraTargets.add(camera2PhotonTrackedTarget);
        } catch (apriltagHasTargetsCheckedException e) {
            // no targets
        }

        try {
            PhotonTrackedTarget camera3PhotonTrackedTarget = getBestTrackedTargetForCameraThree();
            compatableCameraTargets.add(camera3PhotonTrackedTarget);
        } catch (apriltagHasTargetsCheckedException e) {
            // no targets
        }

        if(compatableCameraTargets.size() == 0){
            throw new apriltagHasTargetsCheckedException("No cameras picked up any targets");
        }

        return compatableCameraTargets;
    }
    /*
     * This method returns the ID of the target that the camera is currently tracking
     * It could be used to tell which target it is aimed at so we know where the robot is
     * @return the ID of the target that the camera is currently tracking
     */
    public int getJanuaryTagId(PhotonTrackedTarget theTarget){
        return theTarget.getFiducialId();
    }

    /*
     * This is a method to return data given from a Transform3d
     * @return an ArrayList<Double> with values about the Transform3d, assuming the camera is (0,0)
     * [0] is the x value of the target relative to the camera
     * [1] is the y value of the target relative to the camera
     * [2] is the z value of the target relative to the camera
     */
    public ArrayList<Double> getTransform3dData() throws apriltagHasTargetsCheckedException{
        ArrayList<Double> arr = new ArrayList<Double>();
        Transform3d targetTransform = getBestCameraToTarget(getBestTrackedTarget());
        arr.add(targetTransform.getX());
        arr.add(targetTransform.getY());
        arr.add(targetTransform.getZ());
        return arr;
    }

    /*
     * These next three methods are for setting deadzones for the go() method.
     * For the first two methods @param dz - the deadzone that you want to set
     * For the last method @param x - the x deadzone that you want to set, @param y - the y deadzone that you want to set
     */
    
    public void setDeadzoneX(double dz){
        deadzoneX = dz;
    }

    public void setDeadzoneY(double dz){
        deadzoneY = dz;
    }

    public void setDeadzones(double x, double y){
        deadzoneX = x;
        deadzoneY = y;
    }
    /*
     * This method is the method that you should call to make the robot go to the target. Currently, it will go to any apriltag that it sees, but we can change that later.
     * It uses the getBestTrackedTarget() method to get the best target that the camera is currently tracking,  and then uses it in the Transform3d object.
     * It then uses the Transform3d object to get the x and y values of the target relative to the camera, and then uses those values to calculate the x and y values of the target relative to the robot.
     * If the x and y values of the target relative to the robot are within the deadzone, set in OI, then the robot will stop moving.
     * If the x and y values of the target relative to the robot are not within the deadzone, then the robot will move towards the target using arcade drive formulas.
     */
    // public void go(){
        
    //     while(2>1){
            
    //         Transform3d targetTransform = getBestCameraToTarget(getBestTrackedTarget());
    //         Transform3d scoring = targetTransform.plus(offset);

    //         if(scoring.getX() + deadzoneX > 0 && scoring.getX() - deadzoneX < 0 && scoring.getY() + deadzoneY > 0 && scoring.getY() - deadzoneY < 0){
    //             log("Outtaking");
    //             log("FINAL X: " + scoring.getX());
    //             log("FINAL Y: " + scoring.getY());
    //             break;
    //         }else{
                

    //             double x_scoring = scoring.getX();
    //             double y_scoring = scoring.getY(); 

    //             double yaw_scoring = scoring.getRotation().getZ();
    //             double x_target = x_scoring + Math.cos(yaw_scoring) * (x_targetConstant * y_scoring);
    //             double y_target = y_scoring + Math.sin(yaw_scoring) * (x_targetConstant * y_scoring);

                
    //             double forward = Math.sqrt((y_target * y_target) + (x_target * x_target));
    //             double turn = -Math.tan(y_target/x_target);

                
    //             double leftMotorPower = turnConstant * turn + forwardConstant * MathUtil.clamp(forward, -1, 1);
	// 	        double rightMotorPower = turnConstant * -turn + forwardConstant * MathUtil.clamp(forward, -1, 1);
                
    //             leftMotor.set((-1) * leftMotorPower);
    //             rightMotor.set((-1) * rightMotorPower);

    //             log("X: " + scoring.getX());
    //             log("Y: " + scoring.getY());
    //         }
    //     }
    // }

    /*
     * These are debug logs
     */
    public void debugLogs() throws apriltagHasTargetsCheckedException{
        Transform3d targetTransform = getBestCameraToTarget(getBestTrackedTarget());
        PhotonTrackedTarget theTarget = getBestTrackedTarget();

        log("Yaw PTT " + theTarget.getYaw());
        log("Pitch PTT " + theTarget.getPitch());
        log("Yaw: " + targetTransform.getX());
        log("Pitch: " + targetTransform.getY());
    }

    public ArrayList<Double> logges(){
        return field.logCoords();
    }

}