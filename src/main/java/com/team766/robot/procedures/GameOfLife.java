package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;
import com.team766.library.RateLimiter;

public class GameOfLife extends Procedure {

	static boolean borderless = true;
	private int w = 16;
    private int h = 16;
	boolean[][] grid;
	boolean[][] gridCheck;
	private RateLimiter lifeLimiter;
	
	public enum gameModes {
		RANDOM,
		GLIDER,
		HIVENUDGER
	}

	public void reset(gameModes gameMode) {
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
				gridCheck[5+0][5+0] = true;
				gridCheck[5+1][5+1] = true;
				gridCheck[5+1][5+2] = true;
				gridCheck[5+2][5+0] = true;
				gridCheck[5+2][5+1] = true;
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
		}
		grid = copy(gridCheck);
	}

	public GameOfLife(gameModes gameMode) {
		reset(gameMode);
		lifeLimiter = new RateLimiter(0.1);
	}

	public void run(Context context) {
		int stage = 0;
		while (true) {
			if (lifeLimiter.next()) {
				log("Inside RateLimiter");
				
				switch (stage) {
					case 0: step(); 
						break;
					case 1: context.takeOwnership(Robot.candle);
						output(); 
						context.releaseOwnership(Robot.candle); 
						break;
					case 2: grid = copy(gridCheck);
						break;
				}
				stage = (stage + 1) % 3;
			}
			context.yield();
		}
	}

	private void output() {
		for (int runTimes = 0; runTimes < 20; runTimes++) {
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					outputPixel(i, j);
				}
			}
		}
	}

	private void outputPixel(int i, int j) {
		int k;
		if (i % 2 == 0) {
			k = w * i + j + 8;
		} else {
			k = w * i + w - 1 - j + 8;
		}
		log("i,j" + i +"," + j + "=>" + k);
		if (grid[i][j]) {
			Robot.candle.setColor(0.3, 0.3, 0.3, k, 1);
		} else {
			Robot.candle.setColor(0, 0, 0, k, 1);
		}
	}

	private void step() {
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
				} else {
					gridCheck[i][j] = (neighbors == 3);
				}
			}
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