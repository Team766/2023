package com.team766.robot;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.mechanisms.*;
import com.team766.robot.procedures.*;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * This class is the glue that binds the controls on the physical operator interface to the code
 * that allow control of the robot.
 */
public class OI extends Procedure {

  private JoystickReader joystick0;
  private JoystickReader joystick1;
  private JoystickReader joystick2;

  public OI() {
    loggerCategory = Category.OPERATOR_INTERFACE;

    joystick0 = RobotProvider.instance.getJoystick(0);
    joystick1 = RobotProvider.instance.getJoystick(1);
    joystick2 = RobotProvider.instance.getJoystick(2);
    CameraServer.startAutomaticCapture();
  }

  public void run(Context context) {
    while (true) {
      // wait for driver station data (and refresh it using the WPILib APIs)
      context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
      RobotProvider.instance.refreshDriverStationData();

      // Add driver controls here - make sure to take/release ownership
      // of mechanisms when appropriate.
      /*context.takeOwnership(Robot.drive);
      Robot.drive.setArcadeDrivePower(
        joystick0.getAxis(2),
        -1 * joystick0.getAxis(1)
      );
      context.releaseOwnership(Robot.drive);
      */
      // log("Is there a target? " + Robot.photonVision.hasTarget());
      // log the x,y,z, and angle of the target
      context.takeOwnership(Robot.photonVision);
      try {
        Pose3d pose = Robot.photonVision.getPose3d();
        if (pose != null) {
          log(
            "X: " +
            pose.getX() +
            "\n Y: " +
            pose.getY() +
            "\n Z: " +
            pose.getZ()
          );
        } else {
          // log("No pose");
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      context.releaseOwnership(Robot.photonVision);
    }
    //REV A code
    /*context.takeOwnership(Robot.PhotonVisionRevA);
    HashMap<Double, Pose3d> poses = Robot.PhotonVisionRevA.getHashPoses();
    if (poses != null) {
      for (Map.Entry<Double, Pose3d> entry : poses.entrySet()) {
        log(
          "X: " +
          entry.getValue().getX() +
          "\n Y: " +
          entry.getValue().getY() +
          "\n Z: " +
          entry.getValue().getZ()
        );
      }
    } else {
      log("No poses");
    } 
  }*/
}
}