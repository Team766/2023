package com.team766.framework;

import java.util.function.Supplier;

public class AutonomousMode {
	private final Supplier<Procedure> m_constructor;
	private final String m_name;

	public AutonomousMode(String name, Supplier<Procedure> constructor) {
		m_constructor = constructor;
		m_name = name;
	}

	public Procedure instantiate() {
		return m_constructor.get();
	}

	public String name() {
		return m_name;
	}

	@Override
	public String toString() {
		return name();
	}

	public AutonomousMode clone() {
		return new AutonomousMode(m_name, m_constructor);
	}
}