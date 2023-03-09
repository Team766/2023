package com.team766.robot.mechanisms;

// import org.photonvision.targeting.PhotonTrackedTarget;
import com.team766.framework.Mechanism;
import com.team766.logging.Category;
import com.team766.robot.Constants.CameraConstants;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.Filesystem;
import java.io.IOException;
import java.util.*;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;

public class PhotonVisionRevA extends Mechanism {

  // Set up cameras and various variables
  PhotonCamera leftCamera;
  PhotonCamera rightCamera;

  Double leftCameraWeight;
  Double rightCameraWeight;

  AprilTagFieldLayout aprilTagFieldLayout;

  Transform3d leftRobotToCam;
  Transform3d rightRobotToCam;

  PhotonPoseEstimator leftPhotonPoseEstimator;
  PhotonPoseEstimator rightPhotonPoseEstimator;

  /**
   * Constructor for PhotonVision.
   */
  public PhotonVisionRevA() {
    loggerCategory = Category.MECHANISMS;

    // Initialize cameras. Make sure to use the same name you used in PhotonVision.
    leftCamera = new PhotonCamera(CameraConstants.FRONT_LEFT_CAMERA_NAME);
    rightCamera = new PhotonCamera(CameraConstants.FRONT_RIGHT_CAMERA_NAME);

    // TODO: Set camera weights
    leftCameraWeight = CameraConstants.FRONT_LEFT_CAMERA_WEIGHT;
    rightCameraWeight = CameraConstants.FRONT_RIGHT_CAMERA_WEIGHT;

    // Initialize field layout.
    try {
      aprilTagFieldLayout =
        new AprilTagFieldLayout(
          Filesystem
            .getDeployDirectory()
            .toPath()
            .resolve(CameraConstants.FIELD_LAYOUT_FILE)
        );
    } catch (IOException e) {
      e.printStackTrace();
    }
    // TODO test if rotation is correct (from robot to camera or camera to robot)
    /*leftRobotToCam = new Transform3d(new Translation3d(0.022, 0.358, 0.838),
				new Rotation3d(0, -15, 135));
		rightRobotToCam = new Transform3d(new Translation3d(0.022, -0.358, 0.838),
				new Rotation3d(0, -15, -135));
		*/
    leftRobotToCam =
      new Transform3d(
        new Translation3d(
          CameraConstants.FRONT_LEFT_CAMERA_OFFSET_X,
          CameraConstants.FRONT_LEFT_CAMERA_OFFSET_Y,
          CameraConstants.FRONT_LEFT_CAMERA_OFFSET_Z
        ),
        new Rotation3d(
          CameraConstants.FRONT_LEFT_CAMERA_OFFSET_PITCH,
          CameraConstants.FRONT_LEFT_CAMERA_OFFSET_YAW,
          CameraConstants.FRONT_LEFT_CAMERA_OFFSET_ROLL
        )
      );
    rightRobotToCam =
      new Transform3d(
        new Translation3d(
          CameraConstants.FRONT_RIGHT_CAMERA_OFFSET_X,
          CameraConstants.FRONT_RIGHT_CAMERA_OFFSET_Y,
          CameraConstants.FRONT_RIGHT_CAMERA_OFFSET_Z
        ),
        new Rotation3d(
          CameraConstants.FRONT_RIGHT_CAMERA_OFFSET_PITCH,
          CameraConstants.FRONT_RIGHT_CAMERA_OFFSET_YAW,
          CameraConstants.FRONT_RIGHT_CAMERA_OFFSET_ROLL
        )
      );
    // Initialize pose estimators.
    leftPhotonPoseEstimator =
      new PhotonPoseEstimator(
        aprilTagFieldLayout,
        PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP,
        leftCamera,
        leftRobotToCam
      );
    rightPhotonPoseEstimator =
      new PhotonPoseEstimator(
        aprilTagFieldLayout,
        PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP,
        rightCamera,
        rightRobotToCam
      );
  }

  /**
   * Pose estimator that returns an EstimatedRobotPose object.
   *
   * @param photonPoseEstimator The PhotonPoseEstimator object to use.
   * @return An EstimatedRobotPose object, which can be null or an estimated pose,
   */
  public Optional<EstimatedRobotPose> poseEstimate(
    PhotonPoseEstimator photonPoseEstimator
  ) {
    return photonPoseEstimator.update();
  }
  
  /**
   * Gets a hashmap of the poses of the robot.
   * 
   * @return A hashmap of the poses of the robot with weights.
   */
   
   public HashMap<Double, Pose3d> getHashPoses(){
    Optional<EstimatedRobotPose> leftEstimate;
    Optional<EstimatedRobotPose> rightEstimate;
    leftEstimate = poseEstimate(leftPhotonPoseEstimator);
    rightEstimate = poseEstimate(rightPhotonPoseEstimator);
    HashMap<Double, Pose3d> poses = new HashMap<Double, Pose3d>();
    if (leftEstimate != null && !leftEstimate.isEmpty()) {
      poses.put(leftEstimate, leftCameraWeight);
    }
    if (rightEstimate != null && !rightEstimate.isEmpty()) {
      poses.put(rightEstimate, rightCameraWeight);
    }
    return poses;
   }

   public Pose3d finalPose(HashMap<Double, Pose3d> poses){
    double x = 0;
    double y = 0;
    double z = 0;
    double pitchSin = 0;
    double pitchCos = 0;
    double yawSin = 0;
    double yawCos = 0;
    double rollSin = 0;
    double rollCos = 0;

    double weights = 0;
    for (HashMap.Entry<Double, Pose3d> entry : poses.entrySet()) {
      x += entry.getValue().getTranslation().getX() * entry.getKey();
      y += entry.getValue().getTranslation().getY() * entry.getKey();
      z += entry.getValue().getTranslation().getZ() * entry.getKey();
      pitchSin += Math.sin(entry.getValue().getRotation().getPitch()) * entry.getKey();
      pitchCos += Math.cos(entry.getValue().getRotation().getPitch()) * entry.getKey();
      yawSin += Math.sin(entry.getValue().getRotation().getYaw()) * entry.getKey();
      yawCos += Math.cos(entry.getValue().getRotation().getYaw()) * entry.getKey();
      rollSin += Math.sin(entry.getValue().getRotation().getRoll()) * entry.getKey();
      rollCos += Math.cos(entry.getValue().getRotation().getRoll()) * entry.getKey();
      weights += entry.getKey();
   }
    x /= weights;
    y /= weights;
    z /= weights;
    pitchSin /= weights;
    pitchCos /= weights;
    yawSin /= weights;
    yawCos /= weights;
    rollSin /= weights;
    rollCos /= weights;
    double pitch = Math.atan2(pitchSin, pitchCos);
    double yaw = Math.atan2(yawSin, yawCos);
    double roll = Math.atan2(rollSin, rollCos);
    return new Pose3d(new Translation3d(x, y, z), new Rotation3d(pitch, yaw, roll));
}
