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
        robotToCam = new Transform3d(new Translation3d(0, 0.0, 0), new Rotation3d(0, 0, 0));
        photonPoseEstimator = new PhotonPoseEstimator(aprilTagFieldLayout,
                PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP, camera, robotToCam);
        // var result = camera.getLatestResult();
    }

    // check if there is any target
    public boolean hasTarget() {
        var result = camera.getLatestResult();
        return (result == null ? false : result.hasTargets());
    }


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

}
