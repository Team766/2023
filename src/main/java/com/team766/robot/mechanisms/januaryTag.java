package com.team766.robot.mechanisms;

import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.library.RateLimiter;
import com.team766.library.ValueProvider;
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
	public januaryTag(){
		camera1 = new PhotonCamera("januaryTag.camera1");
        leftMotor = RobotProvider.instance.getMotor("leftMotor");
        rightMotor = RobotProvider.instance.getMotor("rightMotor");

        //Sample values, we don't know these work at all yet
        x_targetConstant = 0.5;
        turnConstant = 0.2;
        forwardConstant = 0.2;
        offsetX = 10;
        offsetY = 10;
        offset = new Transform3d(new Translation3d(-offsetX, -offsetY, 0), new Rotation3d());
	}


    /*
     * This method returns the best target that the camera is currently tracking
     * This is useful for the transform3d data
     * @return PhotonTrackedTarget - the best target that the camera is currently tracking
     */
    public PhotonTrackedTarget getBestTrackedTarget(){
        var result = camera1.getLatestResult(); //getting the result from the camera
        boolean hasTargets = result.hasTargets(); // checking to see if there are any targets in the camera's view. IF THERE ISN'T AND YOU USE result.getTargets() YOU WILL GET AN ERROR

        if(hasTargets){
            List<PhotonTrackedTarget> targets = result.getTargets(); // getting targets
            
            PhotonTrackedTarget bestTrackedTarget = result.getBestTarget(); // getting the best target that is currently being picked up by the camera so that it can know where it is
            return bestTrackedTarget;
        }else{
            log("No targets? see what i did there");
            throw new januaryTagException("There were no targets that could be picked up by the camera, so I'm gonna have to throw this error here.");
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
     * @return double[] - the data from the best target that the camera is currently tracking
     * [0] is the yaw
     * [1] is the pitch
     * [2] is the area that the apriltag fills the camera view with
     * [3] is the fiducial id of the april tag
     */
    public double[] getPhotonTrackedTargetData(){
        double[] arr = new double[4];
        PhotonTrackedTarget theTarget = getBestTrackedTarget();
        arr[0] = theTarget.getYaw();
        arr[1] = theTarget.getPitch();
        arr[2] = theTarget.getArea();
        arr[3] = theTarget.getFiducialId();
        return arr;
    }
    
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
     */
    public void go(){
        
        while(2>1){
            
            Transform3d targetTransform = getBestCameraToTarget(getBestTrackedTarget());
            Transform3d scoring = targetTransform.plus(offset);

            if(scoring.getX() + deadzoneX > 0 && scoring.getX() - deadzoneX < 0 && scoring.getY() + deadzoneY > 0 && scoring.getY() - deadzoneY < 0){
                log("Outtaking");
                break;
            }else{
                

                double x_scoring = scoring.getX();
                double y_scoring = scoring.getY(); 

                double x_target = x_scoring + Math.cos(scoring.getX()) * (x_targetConstant * y_scoring);
                double y_target = y_scoring + Math.sin(scoring.getX()) * (y_scoring);

                
                double forward = Math.sqrt((y_target * y_target) + (x_target * x_target));
                double turn = -Math.tan(y_target/x_target);

                
                double leftMotorPower = turnConstant * turn + forwardConstant * MathUtil.clamp(forward, -1, 1);
		        double rightMotorPower = turnConstant * turn + forwardConstant * MathUtil.clamp(forward, -1, 1);
                
                leftMotor.set(leftMotorPower);
                rightMotor.set(rightMotorPower);
            }
        }
    }

    public void debugLogs(){

    }

}
