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
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.DriverStation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
	public januaryTag(){
		camera1 = new PhotonCamera("januaryTag.camera1");
        leftMotor = RobotProvider.instance.getMotor("leftMotor");
        rightMotor = RobotProvider.instance.getMotor("rightMotor");
	}

    public PhotonTrackedTarget getBestTrackedTarget(){
        var result = camera1.getLatestResult(); //getting the result from the camera
        boolean hasTargets = result.hasTargets(); // checking to see if there are any targets in the camera's view. IF THERE ISN'T AND YOU USE result.getTargets() YOU WILL GET AN ERROR

        if(hasTargets){
            List<PhotonTrackedTarget> targets = result.getTargets(); // getting targets
            PhotonPipelineResult photonPipelineResult = new PhotonPipelineResult(2, targets); // TODO: Replace latencyMillis with real latency time
            
        
            PhotonTrackedTarget bestTrackedTarget = result.getBestTarget(); // getting the best target that is currently being picked up by the camera so that it can know where it is
            return bestTrackedTarget;
        }else{
            log("No targets? see what i did there");
            throw new januaryTagException("There were no targets that could be picked up by the camera, so I'm gonna have to throw this error here.");
        }
    }

    public double getYaw(){
        PhotonTrackedTarget theTarget = getBestTrackedTarget();
        return theTarget.getYaw();
    }

    public double getPitch(){
        PhotonTrackedTarget theTarget = getBestTrackedTarget();
        return theTarget.getPitch();
    }

    public double getArea(){
        PhotonTrackedTarget theTarget = getBestTrackedTarget();
        return theTarget.getArea();
    }

    public int getTargetID(){
        PhotonTrackedTarget theTarget = getBestTrackedTarget();
        return theTarget.getFiducialId();
        
    }

    // work in progress
    public void CTC(){
        PhotonTrackedTarget theTarget = getBestTrackedTarget();
        Transform3d CTC = theTarget.getBestCameraToTarget();
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

    public void findTargets(double x_scoring, double y_scoring){
        double robotX_pos = getYaw();
        double robotY_pos = getPitch();

        double delta_x = x_scoring - robotX_pos;
        x_target = x_scoring;
        y_target = y_scoring - (0.2) * Math.abs(delta_x);
    }

    public void go(double x_scoring, double y_scoring){
        while(2>1){
            if(getYaw() + deadzoneX > x_target && getYaw() - deadzoneX < x_target && getPitch() + deadzoneY > y_target && getPitch() - deadzoneY < y_target){
                log("Outtaking");
                break;
            }else{
                double robotX_pos = getYaw();
                double robotY_pos = getPitch();

                double delta_x = x_scoring - robotX_pos;
                double forward = x_scoring;
                double turn = y_scoring - (0.2) * Math.abs(delta_x);

                
                double leftMotorPower = (0.2) * turn + (0.2) * forward;
		        double rightMotorPower = (-0.2) * turn + (0.2) * forward;
                
                leftMotor.set(leftMotorPower);
                rightMotor.set(rightMotorPower);
            }
        }
    }



    

    public void debugLogs(){

    }

}
