// todo: change variable names from firstJoint to joint

package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.logging.Severity;

//This is for the motor that controls the pulley
public class Arms extends Mechanism {

    public ArmJoint firstJoint;
    private ArmJointConfig firstJointConfig;
    
    /**
     * Initialize Arms composed of joints and other things
     * 
     * NOTE: motors are configured from config file
     * @throws Exception
     */
    public Arms() throws Exception {
        firstJointConfig = new ArmJointConfig(
            -25, 45,
            0.00008599997090641409d,
            0,
            0,
            0.0008699999307282269d,
            -0.5, 0.5,
            0, 3000,
            2000);

        firstJoint = new ArmJoint(
            this,
            RobotProvider.instance.getMotor("arms.firstJoint"),
            firstJointConfig);
    }

    // resetting the encoder distance to zero for use without absolutes
    public void resetEncoders() {
        checkContextOwnership();
        firstJoint.resetMotorEncoderPosition();
    }

    /**
     * Set angle relative to parent structure
     * - parent structure of firstJoint is the base
     * - parent structure of secondJoint is the first arm section
     * - 0 degree should up straight up
     * @param firstJointAngle angle in degrees
     * @param secondJointAngle angle in degrees
     */
    public void setArmAngles(float firstJointAngle, float secondJointAngle) {

        log(Severity.INFO, "setArmAngles : " + firstJointAngle + " , " + secondJointAngle);

        firstJoint.setMotorPosition(firstJointAngle);

        // TODO: second joint
    }

    public void periodicUpdate() {
        firstJoint.periodicUpdate();

        // TODO: run 
    }
    
    // TODO: set ffa in spark
	// getter method for getting the encoder position of arm 2
    // public double findEU(){
    //     return secondJoint.getSensorPosition();
    // }
	// // antigrav
    // public void setFfA(){ // Use Encoder Units to Radians in the sine
    //     firstJoint.set((-Math.sin((Math.PI / 88) * firstJoint.getSensorPosition())) * .021);
        
    //     log("ff: " + (-Math.sin(Math.PI / 88) * firstJoint.getSensorPosition()) * .021);
    // }

    // public void setFfB(){
    //     secondJoint.set((-Math.sin((Math.PI / 88) * findEU())) * .011);
    // }

    

	
    // public boolean checkLimits(double a1_pos, double a2_pos){
    //     if(a1_pos < 40 && a1_pos > -30 && a2_pos > -40 && a2_pos < 40 ){
    //         return true;
    //     } else {
    //         firstJoint.set((-Math.sin((Math.PI / 88) * firstJoint.getSensorPosition())) * .021);
    //         secondJoint.set((-Math.sin((Math.PI / 88) * findEU())) * .011);
    //         return false;
    //     }
        
    // }

    
    }
