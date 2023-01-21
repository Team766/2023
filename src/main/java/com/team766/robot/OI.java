package com.team766.robot;

import com.team766.framework.Procedure;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest;
import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.mechanisms.*;
import com.team766.robot.procedures.*;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
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
	}
	
	public void run(Context context) {
		context.takeOwnership(Robot.candle);
		context.takeOwnership(Robot.drive);

		while (true) {
			DriverStation.refreshData();

			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.
			if(joystick0.getButton(8)){
				Robot.candle.setColor(1, 0, 0,5);
			}else if(joystick0.getButton(9)){
				Robot.candle.setColor(0, 1, 0);
			}else if(joystick0.getButton(10)){
				Robot.candle.setColor(0, 0, 1);
			}
			Robot.drive.setArcadeDrivePower(joystick0.getAxis(1), joystick0.getAxis(0));
			
/* 
			
			switch (joystick0.getPOV()) {
				case 0: Robot.candle.FireAnim();
				break;

				case 45: Robot.candle.ColorFlowAnim();
				break;

				case 90: Robot.candle.Larson();
				break;

				case 135: Robot.candle.Rainbow();
				break;

				case 180: Robot.candle.RgbFade();
				break;

				case 225: Robot.candle.SingleFade();
				break;

				case 270: Robot.candle.Strobe();
				break;

				case 315: Robot.candle.Twinkle();
				break;

				case -1: Robot.candle.setColor(0, 0, 0);;
				break;


				default: Robot.candle.EndAnim();
			}
*/
			if(joystick0.getButton(1)){
				Robot.candle.CustomAnim();
			}
			
			
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
		}
	}
}
