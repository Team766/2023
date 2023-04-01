package com.team766.hal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CanivReader implements Runnable {
	private final InputStream in;

	public CanivReader(InputStream in) {
		this.in = in;
	}

	@Override
	public void run() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			List<String> lines = new ArrayList<String>();

			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				lines.add(line);
			}
			SmartDashboard.putStringArray("caniv", lines.toArray(new String[0]));
			reader.close();	
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
