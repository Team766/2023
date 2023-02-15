package com.team766.robot.procedures;

import java.util.function.BooleanSupplier;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.RobotProvider;
import com.team766.robot.Robot;
import com.team766.library.RateLimiter;

public class GameOfLife extends Procedure {

	static boolean borderless = true;
	private int w = 16;
    private int h = 16;
	boolean[][] grid;
	boolean[][] gridCheck;
	private RateLimiter lifeLimiter;
	private Context golContext;
	
	public enum gameModes {
		RANDOM,
		GLIDER,
		HIVENUDGER,
		BEAR
	}

	public void newMode(gameModes gameMode) {
		switch (gameMode) {
			case RANDOM:
				borderless = true;
				grid = new boolean[h + (borderless? 0 : 1)][w + (borderless? 0 : 1)];
				gridCheck = new boolean[h + (borderless? 0 : 1)][w + (borderless? 0 : 1)];
				for (int i = 0; i < h; i++) {
					for (int j = 0; j < w; j++) {
					gridCheck[i][j] = Math.random() > 0.5;
					}
				}
			break;

			case GLIDER:
				borderless = true;
				grid = new boolean[h + (borderless? 0 : 1)][w + (borderless? 0 : 1)];
				gridCheck = new boolean[h + (borderless? 0 : 1)][w + (borderless? 0 : 1)];
				gridCheck[0][0] = true;
				gridCheck[1][1] = true;
				gridCheck[1][2] = true;
				gridCheck[2][0] = true;
				gridCheck[2][1] = true;
			break;

			case HIVENUDGER:
				borderless = true;
				grid = new boolean[h + (borderless? 0 : 1)][w + (borderless? 0 : 1)];
				gridCheck = new boolean[h + (borderless? 0 : 1)][w + (borderless? 0 : 1)];
				gridCheck[1][1] = true;
				gridCheck[1][2] = true;
				gridCheck[1][3] = true;
				gridCheck[1][4] = true;
				gridCheck[1][10] = true;
				gridCheck[1][13] = true;
				
				gridCheck[2][1] = true;
				gridCheck[2][5] = true;
				gridCheck[2][9] = true;
				
				gridCheck[3][1] = true;
				gridCheck[3][9] = true;
				gridCheck[3][13] = true;
				
				gridCheck[4][2] = true;
				gridCheck[4][5] = true;
				gridCheck[4][9] = true;
				gridCheck[4][10] = true;
				gridCheck[4][11] = true;
				gridCheck[4][12] = true;
		
				gridCheck[6][6] = true;
				gridCheck[6][7] = true;
				gridCheck[7][6] = true;
				gridCheck[7][7] = true;
				gridCheck[8][6] = true;
				gridCheck[8][7] = true;
		
				gridCheck[10][2] = true;
				gridCheck[10][5] = true;
				gridCheck[10][9] = true;
				gridCheck[10][10] = true;
				gridCheck[10][11] = true;
				gridCheck[10][12] = true;
				
				gridCheck[11][1] = true;
				gridCheck[11][9] = true;
				gridCheck[11][13] = true;
		
				gridCheck[12][1] = true;
				gridCheck[12][5] = true;
				gridCheck[12][9] = true;
				
				gridCheck[13][1] = true;
				gridCheck[13][2] = true;
				gridCheck[13][3] = true;
				gridCheck[13][4] = true;
				gridCheck[13][10] = true;
				gridCheck[13][13] = true;
			break;

			case BEAR:
				borderless = true;
				grid = new boolean[h + (borderless? 0 : 1)][w + (borderless? 0 : 1)];
				gridCheck = new boolean[h + (borderless? 0 : 1)][w + (borderless? 0 : 1)];
				gridCheck[0][2] = true;
				gridCheck[0][3] = true;
				gridCheck[0][4] = true;
				
				gridCheck[1][1] = true;
				gridCheck[1][5] = true;
				gridCheck[1][6] = true;
				gridCheck[1][7] = true;
				gridCheck[1][8] = true;
				gridCheck[1][9] = true;

				gridCheck[2][1] = true;
				gridCheck[2][4] = true;
				gridCheck[2][10] = true;
				gridCheck[2][11] = true;
				gridCheck[2][12] = true;

				gridCheck[3][1] = true;
				gridCheck[3][3] = true;
				gridCheck[3][13] = true;

				gridCheck[4][2] = true;
				gridCheck[4][6] = true;
				gridCheck[4][14] = true;

				gridCheck[5][2] = true;
				gridCheck[5][6] = true;
				gridCheck[5][7] = true;
				gridCheck[5][14] = true;

				gridCheck[6][2] = true;
				gridCheck[6][15] = true;
				
				gridCheck[7][3] = true;
				gridCheck[7][10] = true;
				gridCheck[7][11] = true;
				gridCheck[7][15] = true;

				gridCheck[8][3] = true;
				gridCheck[8][10] = true;
				gridCheck[8][11] = true;
				gridCheck[8][15] = true;

				gridCheck[9][2] = true;
				gridCheck[9][15] = true;

				gridCheck[10][2] = true;
				gridCheck[10][6] = true;
				gridCheck[10][7] = true;
				gridCheck[10][14] = true;

				gridCheck[11][2] = true;
				gridCheck[11][6] = true;
				gridCheck[11][14] = true;

				gridCheck[12][1] = true;
				gridCheck[12][3] = true;
				gridCheck[12][13] = true;

				gridCheck[13][1] = true;
				gridCheck[13][4] = true;
				gridCheck[13][10] = true;
				gridCheck[13][11] = true;
				gridCheck[13][12] = true;

				gridCheck[14][1] = true;
				gridCheck[14][5] = true;
				gridCheck[14][6] = true;
				gridCheck[14][7] = true;
				gridCheck[14][8] = true;
				gridCheck[14][9] = true;

				gridCheck[15][2] = true;
				gridCheck[15][3] = true;
				gridCheck[15][4] = true;
			break;
		}
		grid = copy(gridCheck);
	}

	public void reset(gameModes gameMode) {
		newMode(gameMode);
		golContext.takeOwnership(Robot.candle);
		Robot.candle.setColor(0, 0, 0, 8, w * h);
		int k;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (grid[i][j]) {
					if (i % 2 == 0) {
						k = h * i + j + 8;
					} else {
						k = h * i + w - 1 - j + 8;
					}
					Robot.candle.setColor(0.3, 0.3, 0.3, k, 1);
				}
			}
			golContext.waitForSeconds(0.001);
		}
		golContext.releaseOwnership(Robot.candle);
	}

	public GameOfLife(gameModes gameMode) {
		newMode(gameMode);
		lifeLimiter = new RateLimiter(0.1);
	}

	public void run(Context context) {
		golContext = context;
		int stage = 0;
		while (true) {
			if (lifeLimiter.next()) {
				log("Inside RateLimiter");
				switch (stage) {
					case 0: context.takeOwnership(Robot.candle);
						step(context); 
						context.releaseOwnership(Robot.candle); 
						break;
					case 1: grid = copy(gridCheck);
						break;
				}
				stage = (stage + 1) % 2;
			}
			context.yield();
		}
	}

	private void step(Context context) {
		int neighbors;
		int k = 0;

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (i % 2 == 0) {
					k = h * i + j + 8;
				} else {
					k = h * i + w - 1 - j + 8;
				}		
				neighbors = 0;
				neighbors += (grid[wrapAround(i - 1, h)][wrapAround(j - 1, w)]? 1 : 0);
				neighbors += (grid[wrapAround(i - 1, h)][j]? 1 : 0);
				neighbors += (grid[wrapAround(i - 1, h)][wrapAround(j + 1, w)]? 1 : 0);
				neighbors += (grid[i][wrapAround(j - 1, w)]? 1 : 0);
				neighbors += (grid[i][wrapAround(j + 1, w)]? 1 : 0);
				neighbors += (grid[wrapAround(i + 1, h)][wrapAround(j - 1, w)]? 1 : 0);
				neighbors += (grid[wrapAround(i + 1, h)][j]? 1 : 0);
				neighbors += (grid[wrapAround(i + 1, h)][wrapAround(j + 1, w)]? 1 : 0);
				/*if (neighbors > 0 && !grid[i][j]) {
					switch (neighbors) {
						case 1: Robot.candle.setColor(0.3,0,0,k,1); break;
						case 2: Robot.candle.setColor(0,0.3,0,k,1); break;
						case 3: Robot.candle.setColor(0,0,0.3,k,1); break;
						case 4: Robot.candle.setColor(0.3,0.2,0,k,1); break;
						case 5: Robot.candle.setColor(0.3,0,0.3,k,1); break;
					}
				}*/
				if (grid[i][j]) {
					gridCheck[i][j] = (neighbors == 2 || neighbors == 3);
					if (!gridCheck[i][j]) {
						Robot.candle.setColor(0, 0, 0, k, 1);
					}
				} else {
					gridCheck[i][j] = (neighbors == 3);
					if (gridCheck[i][j]) {
						Robot.candle.setColor(0.3, 0.3, 0.3, k, 1);
					}
				}
			}
			context.waitForSeconds(0.001);
		}
		grid = copy(gridCheck);
	}

	int wrapAround(int input, int limit) {
		if (input == limit) {
		  return borderless? 0 : limit;
		}
		if (input == -1) {
		  return borderless? limit - 1 : limit;
		}
		return input;
	  }
	
	  boolean[][] copy(boolean[][] input) {
		boolean[][] newArr = new boolean[input.length][input[0].length];
		for (int m = 0; m < input.length; m++) {
		  for (int n = 0; n < input[0].length; n++) {
			newArr[m][n] = input[m][n];
		  }
		}
		return newArr;
	  }
	
}