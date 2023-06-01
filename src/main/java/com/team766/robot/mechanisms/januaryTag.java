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
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Transform3d;
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

    public void setXtargetConstant(double constant){
        x_targetConstant = constant;
    }

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

    public Transform3d getBestCameraToTarget(PhotonTrackedTarget target){
        return target.getBestCameraToTarget();
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

    public void go(){
        Transform3d targetTransform = getBestCameraToTarget(getBestTrackedTarget());
        while(2>1){
            if(targetTransform.getX() + deadzoneX - offsetX > 0 && targetTransform.getX() - deadzoneX - offsetX < 0 && targetTransform.getY() + deadzoneY - offsetY > 0 && targetTransform.getY() - deadzoneY -offsetY < 0){
                log("Outtaking");
                break;
            }else{
                

                double x_scoring = targetTransform.getX() - offsetX;
                double y_scoring = targetTransform.getY() - offsetY; 

                double x_target = x_scoring + Math.cos(targetTransform.getX()) * (x_targetConstant * y_scoring);
                double y_target = y_scoring + Math.sin(targetTransform.getX()) * (y_scoring);

                
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
