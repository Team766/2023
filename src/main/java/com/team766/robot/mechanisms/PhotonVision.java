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
import edu.wpi.first.wpilibj.Filesystem;

public class PhotonVision extends Mechanism {
    PhotonCamera camera;
    AprilTagFieldLayout aprilTagFieldLayout;
    Transform3d robotToCam;
    PhotonPoseEstimator photonPoseEstimator;
    // List<PhotonTrackedTarget> targets;
    // PhotonTrackedTarget target;

    public PhotonVision() {
        loggerCategory = Category.MECHANISMS;

        // Create camera object
        camera = new PhotonCamera("Camera1");
        try {
            aprilTagFieldLayout = new AprilTagFieldLayout(
                    Filesystem.getDeployDirectory().toPath().resolve("Field.JSON"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        robotToCam = new Transform3d(new Translation3d(0, 0.0, 3), new Rotation3d(0, 0, 0));
        photonPoseEstimator = new PhotonPoseEstimator(aprilTagFieldLayout,
                PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP, camera, robotToCam);
        // var result = camera.getLatestResult();
    }

    // check if there is any target
    public boolean hasTarget() {
        var result = camera.getLatestResult();
        return (result == null ? false : result.hasTargets());
    }

    /*
     * //return best target
     * public void update(){
     * if(hasTarget()){
     * targets = camera.getLatestResult().getTargets();
     * target = camera.getLatestResult().getBestTarget();
     * //log(target.toString());
     * }
     * }
     * 
     * //return a list of all targets
     * public List<PhotonTrackedTarget> getAllTargets(){
     * return targets;
     * }
     * 
     * //return the id of the target
     * public int getID(){
     * return target.getFiducialId();
     * }
     * 
     * //return a list of x,y,z, and angle from transform3d
     * public List<Double> getXYZAngle(){
     * if(hasTarget()){
     * update();
     * Transform3d target3D = target.getBestCameraToTarget();
     * //log("3D data: " + target3D.toString());
     * 
     * List<Double> xyz = new ArrayList<Double>();
     * xyz.add(target3D.getTranslation().getX());
     * xyz.add(target3D.getTranslation().getY());
     * xyz.add(target3D.getTranslation().getZ());
     * xyz.add(target3D.getRotation().getAngle());
     * return xyz;
     * }
     * return null;
     * }
     */
    public Optional<EstimatedRobotPose> poseEstimate() {
        return photonPoseEstimator.update();
    }

    // public boolean hasPose(){
    // if(poseEstimate() != null) return poseEstimate().isPresent();
    // return false;
    // }
    public Pose3d getPose3d() {
        Optional<EstimatedRobotPose> estimate;
        estimate = poseEstimate();
        if (estimate == null || estimate.isEmpty()) {
            return null;
        }
        return estimate.get().estimatedPose;
    }

    /*
     * //returns robot x and y coords using gyro angle, target angle, target
     * location, and distance
     * //based off of field coordinate system
     * (https://firstfrc.blob.core.windows.net/frc2023/FieldAssets/
     * 2023LayoutMarkingDiagram.pdf)
     * public XY robotPosition() {
     * //todo: use Adrian's point instead of mine
     * //todo: possibly use a direct distance output instead of pythagorean
     * //todo: (not urgent): make it 3d
     * FieldInfoManager fieldData = new
     * FieldInfoManager(FieldInfoManager.Mode.TEST1);
     * AprilTag tag = fieldData.getTagForID(getID());
     * double dist = Math.sqrt(Math.pow(getXYZAngle().get(0), 2) +
     * Math.pow(getXYZAngle().get(1), 2));
     * double angle = getXYZAngle().get(3); //+ Robot.gyro.getGyroYaw();
     * return new XY(tag.getX() - dist * Math.cos(angle), tag.getY() - dist *
     * Math.sin(angle));
     * }
     */
}
