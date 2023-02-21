package com.team766.robot;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import com.team766.robot.mechanisms.*;

public class Robot {

	// Declare mechanisms here
	public static Arms arms;

	

	public static void robotInit() {
		Logger logger = Logger.get(Category.MECHANISMS);

		try {
		// Initialize mechanisms here
		arms = new Arms();
		
		} catch(Exception e) {
			logger.logRaw(Severity.ERROR, "Error in robotInit: " + e.toString());
		}
	}
}
