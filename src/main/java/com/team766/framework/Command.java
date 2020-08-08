package com.team766.framework;

import java.util.LinkedList;

public abstract class Command implements Runnable {
	private boolean m_running = false;
	private LinkedList<Mechanism> m_controlledMechanisms = new LinkedList<Mechanism>();
	
	protected void initialize() {}
	protected void cleanup() {}
	
	public final void start() {
		for (Mechanism m : m_controlledMechanisms) {
			m.takeControl(this);
		}
		initialize();
		m_running = true;
		Scheduler.getInstance().add(this);
	}
	
	public final void stop() {
		Scheduler.getInstance().cancel(this);
		m_running = false;
		cleanup();
	}
	
	protected void takeControl(Mechanism mechanism) {
		m_controlledMechanisms.add(mechanism);
		if (m_running) {
			mechanism.takeControl(this);
		}
	}
}
