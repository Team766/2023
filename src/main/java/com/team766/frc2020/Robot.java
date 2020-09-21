package com.team766.frc2020;

import com.team766.framework.AutonomousProcedureUtils;
import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.frc2020.mechanisms.*;
import com.team766.hal.MyRobot;

import com.team766.web.AutonomousSelector;
import com.team766.web.ConfigUI;
import com.team766.web.LogViewer;
import com.team766.web.WebServer;

public class Robot extends MyRobot {
    // Declare mechanisms here
    
	
	public static OI m_oi;
	
	private WebServer m_webServer;
	private AutonomousSelector m_autonSelector;
    private Context m_autonomous;
    private Context m_oiContext;

	
	@Override
	public void robotInit() {
		// Initialize mechanisms here
        
        
        m_oi = new OI();

		// Auton picker
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
		if (m_oiContext != null) {
			m_oiContext.stop();
			m_oiContext = null;
		}
		
		Procedure autonomousProcedure = AutonomousProcedureUtils.getProcedure(m_autonSelector.getSelectedAutonMode(AutonomousModes.class));
		m_autonomous = new Context(autonomousProcedure);
	}
	
	@Override
	public void teleopInit() {
		if (m_autonomous != null) {
			m_autonomous.stop();
			m_autonomous = null;
		}
		
		if (m_oiContext == null) {
			m_oiContext = new Context(m_oi);
		}
	}
}
