package com.team766.robot.procedures;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.logging.Severity;
import com.team766.robot.Robot;

import edu.wpi.first.wpilibj.Filesystem;
import java.awt.image.BufferedImage;

public class DisplayImage extends Procedure {

	private final int h = Robot.candle.h;
	private final int w = Robot.candle.w;
	private String file;
	private boolean interlacing;
	private boolean filter = false;

	private int rotation = 0;

	public DisplayImage(String file, boolean interlacing) {
		this.file = file;
		this.interlacing = interlacing;
	}

	public DisplayImage(String file) {
		this.file = file;
		this.interlacing = true;
	}

	public DisplayImage(String file, int rotation, boolean interlacing) {
		this.file = file;
		this.interlacing = interlacing;
		this.rotation = rotation;
	}

	public DisplayImage(String file, int rotation) {
		this.file = file;
		this.interlacing = true;
		this.rotation = rotation;
	}

	public DisplayImage(boolean filter, String file, boolean interlacing) {
		this.file = file;
		this.interlacing = interlacing;
		this.filter = filter;
	}

	public DisplayImage(boolean filter, String file) {
		this.file = file;
		this.interlacing = true;
		this.filter = filter;
	}

	public DisplayImage(boolean filter, String file, int rotation, boolean interlacing) {
		this.file = file;
		this.interlacing = interlacing;
		this.rotation = rotation;
		this.filter = filter;
	}

	public DisplayImage(boolean filter, String file, int rotation) {
		this.file = file;
		this.interlacing = true;
		this.rotation = rotation;
		this.filter = filter;
	}

	public void run(Context context) {
		File fileObj;
		Path path = Filesystem.getDeployDirectory().toPath().resolve(file);
		fileObj = path.toFile();
		BufferedImage image;
		Color[][] colors = new Color[h][w];

		try {
			image = ImageIO.read(fileObj);
		} catch (IOException e) {
			e.printStackTrace();
			log(Severity.ERROR, "Could not load " + file);
			return;
		}

		switch (rotation) {
			case 1:
				for (int i = 0; i < h; i++) {
					for (int j = 0; j < w; j++) {
						colors[j][h - i - 1] = new Color(image.getData().getPixel(j, i, (double[]) null));
					}
				}
				break;
			case 2:
				for (int i = 0; i < h; i++) {
					for (int j = 0; j < w; j++) {
						colors[h - i - 1][w - j - 1] = new Color(image.getData().getPixel(j, i, (double[]) null));
					}
				}
				break;
			case 3:
				for (int i = 0; i < h; i++) {
					for (int j = 0; j < w; j++) {
						colors[w - j - 1][i] = new Color(image.getData().getPixel(j, i, (double[]) null));
					}
				}
				break;
			default:
				for (int i = 0; i < h; i++) {
					for (int j = 0; j < w; j++) {
						colors[i][j] = new Color(image.getData().getPixel(j, i, (double[]) null));
					}
				}
		}

		if (filter) {
			int min = 100;
			int max = 255;
			double factor = max - min;
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					colors[i][j].r = (short) ((Math.min(Math.max(min, colors[i][j].r), max) - min) * (255 / factor));
					colors[i][j].g = (short) ((Math.min(Math.max(min, colors[i][j].g), max) - min) * (255 / factor));
					colors[i][j].b = (short) ((Math.min(Math.max(min, colors[i][j].b), max) - min) * (255 / factor));
				}
			}
		}

		display(colors, context, interlacing);
	}

	public void display(Color[][] colors, Context context, boolean interlacing) {
		if (interlacing) {
			for (int i = 0; i < h; i += 2) {
				for (int j = 0; j < w; j++) {
					log(colors[i][j].r + " " + colors[i][j].g + " " + colors[i][j].b);
					context.takeOwnership(Robot.candle);
					Robot.candle.setColor(colors[i][j].r, colors[i][j].g, colors[i][j].b,
							Robot.candle.getMatrixID(i, j), 1);
					context.releaseOwnership(Robot.candle);
				}
				context.waitForSeconds(0.01);
			}
			for (int i = 1; i < h; i += 2) {
				for (int j = 0; j < w; j++) {
					log(colors[i][j].r + " " + colors[i][j].g + " " + colors[i][j].b);
					context.takeOwnership(Robot.candle);
					Robot.candle.setColor(colors[i][j].r, colors[i][j].g, colors[i][j].b,
							Robot.candle.getMatrixID(i, j), 1);
					context.releaseOwnership(Robot.candle);
				}
				context.waitForSeconds(0.01);
			}
		} else {
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					log(colors[i][j].r + " " + colors[i][j].g + " " + colors[i][j].b);
					context.takeOwnership(Robot.candle);
					Robot.candle.setColor(colors[i][j].r, colors[i][j].g, colors[i][j].b,
							Robot.candle.getMatrixID(i, j), 1);
					context.releaseOwnership(Robot.candle);
				}
				context.waitForSeconds(0.0001);
			}
		}
	}

	public class Color {
		public short r;
		public short g;
		public short b;

		public Color(short r, short g, short b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public Color(double[] color) {
			r = (short) color[0];
			g = (short) color[1];
			b = (short) color[2];
		}
	}
}
