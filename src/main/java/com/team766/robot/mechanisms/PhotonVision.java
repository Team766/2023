package com.team766.robot.mechanisms;

import java.io.IOException;
import java.util.*;
import org.photonvision.PhotonCamera;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonPoseEstimator;
//import org.photonvision.targeting.PhotonTrackedTarget;
import com.team766.framework.Mechanism;
import com.team766.logging.Category;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;



public class PhotonVision extends Mechanism {	
    PhotonCamera camera;
    //List<PhotonTrackedTarget> targets;
    //PhotonTrackedTarget target;
    
    public PhotonVision(){
        loggerCategory = Category.MECHANISMS;

        //Create camera object
        camera = new PhotonCamera("Camera1");
        
        //var result = camera.getLatestResult();
    }
    
    //check if there is any target
    public boolean hasTarget(){
        var result = camera.getLatestResult();
        return (result == null ? false : result.hasTargets());
    }
    /* 
    //return best target
    public void update(){
        if(hasTarget()){
            targets = camera.getLatestResult().getTargets();
            target = camera.getLatestResult().getBestTarget();
            //log(target.toString());
        }
    }
    
    //return a list of all targets
    public List<PhotonTrackedTarget> getAllTargets(){
        return targets;
    }

    //return the id of the target
    public int getID(){
        return target.getFiducialId();
    }

    //return a list of x,y,z, and angle from transform3d
    public List<Double> getXYZAngle(){
        if(hasTarget()){
            update();
            Transform3d target3D = target.getBestCameraToTarget();
            //log("3D data: " + target3D.toString());
            
            List<Double> xyz = new ArrayList<Double>();
            xyz.add(target3D.getTranslation().getX());
            xyz.add(target3D.getTranslation().getY());
            xyz.add(target3D.getTranslation().getZ());
            xyz.add(target3D.getRotation().getAngle());
            return xyz;
        }
        return null;
    }
    */
    public Optional<EstimatedRobotPose> PoseEstimate() throws IOException{
        AprilTagFieldLayout aprilTagFieldLayout = new AprilTagFieldLayout("Field.JSON");
        Transform3d robotToCam = new Transform3d(new Translation3d(0.5, 0.0, 0.5), new Rotation3d(0,0,0)); //Cam mounted facing forward, half a meter forward of center, half a meter up from center.
        PhotonPoseEstimator photonPoseEstimator = new PhotonPoseEstimator(aprilTagFieldLayout, PhotonPoseEstimator.PoseStrategy.AVERAGE_BEST_TARGETS, camera, robotToCam);
        return photonPoseEstimator.update();
    }
    public boolean hasPose(){
        try {
            return PoseEstimate().isPresent();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public Pose3d getPose3d(){
        Optional<EstimatedRobotPose> estimate;
        try {
            estimate = PoseEstimate();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if(estimate.isEmpty()) {
            return null;
        }
        return estimate.get().estimatedPose;
    }


    /*
    //returns robot x and y coords using gyro angle, target angle, target location, and distance
    //based off of field coordinate system (https://firstfrc.blob.core.windows.net/frc2023/FieldAssets/2023LayoutMarkingDiagram.pdf)
    public XY robotPosition() {
        //todo: use Adrian's point instead of mine
        //todo: possibly use a direct distance output instead of pythagorean
        //todo: (not urgent): make it 3d
        FieldInfoManager fieldData = new FieldInfoManager(FieldInfoManager.Mode.TEST1);
        AprilTag tag = fieldData.getTagForID(getID());
        double dist = Math.sqrt(Math.pow(getXYZAngle().get(0), 2) + Math.pow(getXYZAngle().get(1), 2));
        double angle =  getXYZAngle().get(3); //+ Robot.gyro.getGyroYaw();
        return new XY(tag.getX() - dist * Math.cos(angle), tag.getY() - dist * Math.sin(angle));
    }
    */
}
