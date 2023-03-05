package com.team766.robot.procedures;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.logging.Severity;
import com.team766.robot.Robot;
import com.team766.robot.procedures.DisplayImage;

import edu.wpi.first.wpilibj.Filesystem;
import java.awt.image.BufferedImage;

public class PlayAnimation extends Procedure {

	private String folder;
	private int length = 1;
	private double framerate;
	private boolean loop;

	public PlayAnimation(String folder, int length, double framerate, boolean loop) {
		this.folder = folder;
		this.framerate = framerate;
		this.length = length;
		this.loop = loop;
	}


	public void run(Context context) {
		do {
			for (int i = 0; i < length; i++) {
				context.startAsync(new DisplayImage(folder + Integer.toString(i % 5000) + "/" + i + ".png", true, i % 2));
				context.yield();
				context.waitForSeconds(1 / framerate);
			}
			context.yield();
		} while (loop);
	}

}
