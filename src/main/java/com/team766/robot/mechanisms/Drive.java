package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController;

public class Drive extends Mechanism {
    private MotorController leftMotor;
    private MotorController rightMotor;

    public Drive() {
        leftMotor = RobotProvider.instance.getMotor("drive.leftMotor");
        rightMotor = RobotProvider.instance.getMotor("drive.rightMotor");
    }

    public void setArcadeDrivePower(double forward, double turn) {
        checkContextOwnership();

        // less is moar
        forward *= 0.15;
        turn *= 0.15;

        double leftMotorPower = turn + forward;
        double rightMotorPower = -turn + forward;

        log("left: " + leftMotorPower + ", right: " + rightMotorPower);

        leftMotor.set(leftMotorPower);
        rightMotor.set(rightMotorPower);
    }
}
