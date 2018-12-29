package com.team766.trajectory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.team766.config.ConfigFileReader;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public class ReadPath {
	private Scanner reader;
	
	public Path getPath(String filePath) {
		Logger logger = Logger.get(Category.TRAJECTORY);
		logger.logRaw(Severity.INFO, "Reading trajectory from " + filePath);
		try {
			reader = new Scanner(new File(new File(ConfigFileReader.getInstance().getString("trajectoryPathRoot").get()), filePath));				
		} catch (FileNotFoundException e) {
			logger.logRaw(Severity.ERROR, "Error: Path file " + filePath + " not opened");
			throw new RuntimeException(e);
		}

		String name = reader.nextLine();
		int num_elements = reader.nextInt();

		Trajectory left = new Trajectory(num_elements);
		for (int i = 0; i < num_elements; i++) {
			Trajectory.Segment segment = new Trajectory.Segment();

			segment.pos = reader.nextDouble();
			segment.vel = reader.nextDouble();
			segment.acc = reader.nextDouble();
			segment.jerk = reader.nextDouble();
			segment.heading = reader.nextDouble();
			segment.dt = reader.nextDouble();
			segment.x = reader.nextDouble();
			segment.y = reader.nextDouble();

			left.setSegment(i, segment);
		}
		Trajectory right = new Trajectory(num_elements);
		for (int i = 0; i < num_elements; i++) {
			Trajectory.Segment segment = new Trajectory.Segment();

			segment.pos = reader.nextDouble();
			segment.vel = reader.nextDouble();
			segment.acc = reader.nextDouble();
			segment.jerk = reader.nextDouble();
			segment.heading = reader.nextDouble();
			segment.dt = reader.nextDouble();
			segment.x = reader.nextDouble();
			segment.y = reader.nextDouble();

			right.setSegment(i, segment);
		}

		logger.logRaw(Severity.INFO, "...finished reading trajectory.");
		return new Path(name, new Trajectory.Pair(left, right));
	}
}
