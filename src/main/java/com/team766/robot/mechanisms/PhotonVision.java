package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com. team766.hal.RobotProvider;
import edu.wpi.first.math.geometry.Transform3d;
import org.photonvision.*;
import org.photonvision.targeting.PhotonTrackedTarget;
import java.util.*;
import com.team766.logging.Category;
import com.team766.robot.Robot;
import com.team766.simulator.ProgramInterface.RobotPosition;
import com.team766.apriltags.AprilTag;
import com.team766.apriltags.FieldInfoManager;
import com.team766.apriltags.XY;


public class PhotonVision extends Mechanism {	
    PhotonCamera camera;
    List<PhotonTrackedTarget> targets;
    PhotonTrackedTarget target;
    
    public PhotonVision(){
        loggerCategory = Category.MECHANISMS;
        camera = new PhotonCamera("Camera1");
        var result = camera.getLatestResult();
    }
    
    //check if there is a target
    public boolean hasTarget(){
        var result = camera.getLatestResult();
        return (result == null ? false : result.hasTargets());
    }

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

    //returns robot x and y coords using gyro angle, target angle, target location, and distance
    //based off of field coordinate system (https://firstfrc.blob.core.windows.net/frc2023/FieldAssets/2023LayoutMarkingDiagram.pdf)
    public XY robotPosition() {

        

        //todo: use Adrian's point instead of mine
        //todo: possibly use a direct distance output instead of pythagorean
        //todo: (not urgent): make it 3d
        
        AprilTag tag = FieldInfoManager.getTagForID(getID());
        double dist = Math.sqrt(Math.pow(getXYZAngle().get(0), 2) + Math.pow(getXYZAngle().get(1), 2));
        double angle =  getXYZAngle().get(3) + Robot.gyro.getGyroYaw();
        return new XY(tag.getX() - dist * Math.cos(angle), tag.getY() - dist * Math.sin(angle));
    }
}
