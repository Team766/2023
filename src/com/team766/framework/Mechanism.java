package com.team766.framework;

public abstract class Mechanism implements Runnable {
	public Mechanism() {
		Scheduler.getInstance().add(this);
	}
}
