package com.team766.robot.mechanisms;

import java.io.IOException;
import java.util.*;
import org.photonvision.PhotonCamera;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonPoseEstimator;
// import org.photonvision.targeting.PhotonTrackedTarget;
import com.team766.framework.Mechanism;
import com.team766.logging.Category;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.Filesystem;


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
		leftCamera = new PhotonCamera("leftCamera");
		rightCamera = new PhotonCamera("rightCamera");

		// TODO: Set camera weights
		leftCameraWeight = 1.0;
		rightCameraWeight = 1.0;

		// Initialize field layout.
		try {
			aprilTagFieldLayout = new AprilTagFieldLayout(
					Filesystem.getDeployDirectory().toPath().resolve("Field.JSON"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO: Change these values to match your robot.
		leftRobotToCam = new Transform3d(new Translation3d(0, 0.0, 0), new Rotation3d(0, 0, 0));
		rightRobotToCam = new Transform3d(new Translation3d(0, 0.0, 0), new Rotation3d(0, 0, 0));

		leftPhotonPoseEstimator = new PhotonPoseEstimator(aprilTagFieldLayout,
				PhotonPoseEstimator.PoseStrategy.AVERAGE_BEST_TARGETS, leftCamera, leftRobotToCam);
		rightPhotonPoseEstimator = new PhotonPoseEstimator(aprilTagFieldLayout,
				PhotonPoseEstimator.PoseStrategy.AVERAGE_BEST_TARGETS, rightCamera,
				rightRobotToCam);
	}

	/**
	 * Pose estimator that returns an EstimatedRobotPose object.
	 * 
	 * @param photonPoseEstimator The PhotonPoseEstimator object to use.
	 * @return An EstimatedRobotPose object, which can be null or an estimated pose,
	 */
	public Optional<EstimatedRobotPose> poseEstimate(PhotonPoseEstimator photonPoseEstimator) {
		return photonPoseEstimator.update();
	}

	/**
	 * Get the pose of the robot if possible.
	 * 
	 * @return The pose of the robot.
	 */
	public Pose3d getPose3d() {
		Optional<EstimatedRobotPose> leftEstimate;
		Optional<EstimatedRobotPose> rightEstimate;
		leftEstimate = poseEstimate(leftPhotonPoseEstimator);
		rightEstimate = poseEstimate(rightPhotonPoseEstimator);

		if (leftEstimate == null || leftEstimate.isEmpty()) {
			if (rightEstimate == null || rightEstimate.isEmpty()) {
				return null;
			} else {
				return rightEstimate.get().estimatedPose;
			}
		} else {
			if (rightEstimate == null || rightEstimate.isEmpty()) {
				return leftEstimate.get().estimatedPose;
			}
		}
		HashMap<Pose3d, Double> poses = new HashMap<Pose3d, Double>();
		poses.put(leftEstimate.get().estimatedPose, leftCameraWeight);
		poses.put(rightEstimate.get().estimatedPose, rightCameraWeight);
		return averagPose3d(poses);
	}

	/**
	 * Averages a HashMap of Pose3d objects.
	 * 
	 * @param poses A HashMap of Pose3d objects and their weights.
	 * @return The average Pose3d object or null if there are no weights.
	 */
	public Pose3d averagPose3d(HashMap<Pose3d, Double> poses) {
		int numPoses = poses.size();
		double weights = 0;
		Pose3d averagePose = new Pose3d(0.0, 0.0, 0.0, new Rotation3d(0.0, 0.0, 0.0));
		for (HashMap.Entry<Pose3d, Double> entry : poses.entrySet()) {
			Pose3d pose = entry.getKey();
			Double weight = entry.getValue();
			averagePose = averagePose.plus(new Transform3d(
					new Pose3d(0.0, 0.0, 0.0, new Rotation3d(0.0, 0.0, 0.0)), pose.times(weight)));
			weights += weight.doubleValue();
		}
		if (weights != 0) {
			return averagePose.times(1.0 / weights);
		}
		return null;
	}

}
