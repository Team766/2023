package com.team766;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Scheduler;
import com.team766.hal.RobotProvider;
import com.team766.hal.mock.TestRobotProvider;

public abstract class TestCase extends junit.framework.TestCase {
	
	@Override
	protected void setUp() throws Exception {
		ConfigFileReader.instance = new ConfigFileReader(this.getClass().getClassLoader().getResource("testConfig.txt").getPath());
		RobotProvider.instance = new TestRobotProvider();
		
		Scheduler.getInstance().reset();
	}
	
	protected void step(){
		Scheduler.getInstance().run();
	}

}
