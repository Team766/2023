package com.team766.frc2018;

import com.team766.framework.Command;
import com.team766.frc2018.commands.ExampleDriveSequence;
import com.team766.frc2018.commands.ExampleSequence;
import com.team766.frc2018.mechanisms.Arm;
import com.team766.frc2018.mechanisms.Drive;
import com.team766.frc2018.mechanisms.Wrist;
import com.team766.hal.MyRobot;
import com.team766.web.AutonomousSelector;
import com.team766.web.ConfigUI;
import com.team766.web.LogViewer;
import com.team766.web.WebServer;

public class Robot extends MyRobot {	
	public static Arm arm;
	public static Wrist wrist;
	public static Drive drive;
	
	private OI m_oi;
	
	private WebServer m_webServer;
	private AutonomousSelector m_autonSelector;
	private Command m_autonomous;
	
	@Override
	public void robotInit() {
		drive = new Drive();
		arm = new Arm();
		wrist = new Wrist();
		
		m_webServer = new WebServer();
		m_webServer.addHandler("/config", new ConfigUI());
		m_webServer.addHandler("/logs", new LogViewer());
		m_autonSelector = new AutonomousSelector(AutonomousModes.class);
		m_webServer.addHandler("/values", m_autonSelector);
		m_webServer.start();
	}
	
	@Override
	public void autonomousInit() {
		if (m_autonomous != null) {
			m_autonomous.stop();
		}
		if (m_oi != null) {
			m_oi.stop();
		}
		
		switch (m_autonSelector.getSelectedAutonMode(AutonomousModes.class)) {
		case Autonomous1:
			m_autonomous = new ExampleDriveSequence();
			break;
		case Autonomous2:
			m_autonomous = new ExampleSequence();
			break;
		}
		m_autonomous.start();
	}
	
	@Override
	public void teleopInit() {
		if (m_autonomous != null) {
			m_autonomous.stop();
		}
		
		if (m_oi == null) {
			m_oi = new OI();
		}
		m_oi.start();
	}
}
