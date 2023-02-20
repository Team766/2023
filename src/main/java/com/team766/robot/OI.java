package com.team766.robot;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.framework.LaunchedContext;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.mechanisms.*;
import com.team766.robot.procedures.*;
import com.team766.robot.procedures.GameOfLife.gameModes;

import edu.wpi.first.wpilibj.DriverStation;
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
		Robot.candle.setColor(0, 0, 0);
		context.releaseOwnership(Robot.candle);
		Minesweeper mine = new Minesweeper();
		context.startAsync(mine);
		//GameOfLife gol = new GameOfLife(gameModes.GLIDER);
		//context.startAsync(gol);
		int lastPOV = -1;
		while (true) {
			// wait for driver station data (and refresh it using the WPILib APIs)
			context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
			RobotProvider.instance.refreshDriverStationData();

			// Add driver controls here - make sure to take/release ownership
			// of mechanisms when appropriate.
			//Robot.candle.setColor(1, 0, 0.9);
			/*if(joystick0.getButton(8)){
				Robot.candle.setColor(1, 0, 0.9);
			}else if(joystick0.getButton(9)){
				Robot.candle.setColor(0, 1, 0);
			}else if(joystick0.getButton(10)){
				Robot.candle.setColor(0, 0, 1);
			}
			Robot.drive.setArcadeDrivePower(joystick0.getAxis(1), joystick0.getAxis(0));
			*/
			if (joystick0.getButtonPressed(1)) {
				//gol.reset(gameModes.RANDOM);
				mine.isClicking = true;
			}

			if (joystick0.getButtonPressed(2)) {
				//gol.reset(gameModes.GLIDER);
				mine.i
			}

			if (joystick0.getButtonPressed(3)) {
				//gol.reset(gameModes.HIVENUDGER);
				mine.reset();
			}

			if (joystick0.getButtonPressed(4)) {
				//gol.reset(gameModes.BEAR);
			}
			
			switch (joystick0.getPOV()) {
				case 0:
				if (lastPOV != 0) {
					lastPOV = 0;
					mine.moveUp();
				}
				break;

				case 90:
				if (lastPOV != 90) {
					lastPOV = 90;
					mine.moveRight();
				}
				break;

				case 180:
				if (lastPOV != 180) {
					lastPOV = 180;
					mine.moveLeft();
				}
				break;

				case 270:
				if (lastPOV != 270) {
					lastPOV = 270;
					mine.moveUp();
				}
				break;

				case -1: lastPOV = -1;
				break;
			}

			/*if(joystick0.getButton(1)){
				Robot.candle.Game();
			}*/
			
		}
	}
}
