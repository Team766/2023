package com.team766.framework;

@FunctionalInterface
public interface RunnableWithContext {
	public abstract void run(Context context);
}