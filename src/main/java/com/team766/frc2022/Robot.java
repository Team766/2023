package com.team766.frc2022;

import com.team766.framework.AutonomousProcedureUtils;
import com.team766.framework.Procedure;
import com.team766.framework.Scheduler;
import com.team766.framework.LaunchedContext;
import com.team766.frc2022.mechanisms.*;
import com.team766.hal.MyRobot;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import com.team766.web.AutonomousSelector;
import com.team766.web.ConfigUI;
import com.team766.web.DriverInterface;
import com.team766.web.LogViewer;
import com.team766.web.WebServer;

public class Robot extends MyRobot {
	// Declare mechanisms here
	
	
	private static OI m_oi;
	
	private WebServer m_webServer;
	private AutonomousSelector m_autonSelector;
	private LaunchedContext m_autonomous;
	private LaunchedContext m_oiContext;

	// Reset the autonomous routine if the robot is disabled for more than this
	// number of seconds.
	private static final double RESET_IN_DISABLED_PERIOD = 10.0;
	private double m_disabledModeStartTime;

	public Robot() {
		m_autonSelector = new AutonomousSelector(AutonomousModes.class);
		m_webServer = new WebServer();
		m_webServer.addHandler(new DriverInterface(m_autonSelector));
		m_webServer.addHandler(new ConfigUI());
		m_webServer.addHandler(new LogViewer());
		m_webServer.addHandler(m_autonSelector);
		m_webServer.start();
	}

	@Override
	public void robotInit() {
		// Initialize mechanisms here
		
		
		m_oi = new OI();
	}
	
	@Override
	public void disabledInit() {
		m_disabledModeStartTime = RobotProvider.instance.getClock().getTime();
	}

	@Override
	public void disabledPeriodic() {
		// The robot can enter disabled mode for two reasons:
		// - The field control system set the robots to disabled.
		// - The robot loses communication with the driver station.
		// In the former case, we want to reset the autonomous routine, as there
		// may have been a field fault, which would mean the match is going to
		// be replayed (and thus we would want to run the autonomous routine
		// from the beginning). In the latter case, we don't want to reset the
		// autonomous routine because the communication drop was likely caused
		// by some short-lived (less than a second long, or so) interference;
		// when the communications are restored, we want to continue executing
		// the routine that was interrupted, since it has knowledge of where the
		// robot is on the field, the state of the robot's mechanisms, etc.
		// Thus, we set a threshold on the amount of time spent in autonomous of
		// 10 seconds. It is almost certain that it will take longer than 10
		// seconds to reset the field if a match is to be replayed, but it is
		// also almost certain that a communication drop will be much shorter
		// than 10 seconds.
		double timeInState =
			RobotProvider.instance.getClock().getTime() - m_disabledModeStartTime;
		if (timeInState > RESET_IN_DISABLED_PERIOD && m_autonomous != null) {
			m_autonomous.stop();
			m_autonomous = null;
			Logger.get(Category.AUTONOMOUS).logRaw(Severity.INFO, "Resetting autonomus procedure");
		}
	}
	
	@Override
	public void autonomousInit() {
		if (m_oiContext != null) {
			m_oiContext.stop();
			m_oiContext = null;
		}
		
		if (m_autonomous == null) {
			Procedure autonomousProcedure = AutonomousProcedureUtils.getProcedure(
				m_autonSelector.getSelectedAutonMode(AutonomousModes.class));
			m_autonomous = Scheduler.getInstance().startAsync(autonomousProcedure);
		}
	}
	
	@Override
	public void teleopInit() {
		if (m_autonomous != null) {
			m_autonomous.stop();
			m_autonomous = null;
		}
		
		if (m_oiContext == null) {
			m_oiContext = Scheduler.getInstance().startAsync(m_oi);
		}
	}

	@Override
	public void teleopPeriodic() {
		if (m_oiContext.isDone()) {
			m_oiContext = Scheduler.getInstance().startAsync(m_oi);
			Logger.get(Category.OPERATOR_INTERFACE).logRaw(Severity.WARNING, "Restarting OI context");
		}
	}
}
