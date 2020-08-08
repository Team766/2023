package com.team766.framework;

import java.util.LinkedList;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public abstract class Command implements Runnable {
	private boolean m_running = false;
	private LinkedList<Mechanism> m_controlledMechanisms = new LinkedList<Mechanism>();
	
	private static int c_idCounter = 0;

	private static synchronized int createNewId() {
		return c_idCounter++;
	}
	
	protected final int m_id;

	public Command() {
		m_id = createNewId();
	}

	public String getCommandName() {
		return this.getClass().getName() + "/" + m_id;
	}

	protected void initialize() {}
	protected void cleanup() {}

	public final void start() {
		for (Mechanism m : m_controlledMechanisms) {
			m.takeControl(this);
		}
		Logger.get(Category.COMMANDS).logRaw(Severity.INFO, getCommandName() + " is starting");
		initialize();
		m_running = true;
		Scheduler.getInstance().add(this);
	}
	
	public final void stop() {
		Scheduler.getInstance().cancel(this);
		m_running = false;
		cleanup();
		Logger.get(Category.COMMANDS).logRaw(Severity.INFO, getCommandName() + " is stopping");
	}
	
	protected void takeControl(Mechanism mechanism) {
		m_controlledMechanisms.add(mechanism);
		if (m_running) {
			mechanism.takeControl(this);
		}
	}
}
