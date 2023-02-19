package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;
import edu.wpi.first.math.Pair;
import com.team766.library.RateLimiter;

public class Minesweeper extends Procedure {

	private int w = 16;
    private int h = 16;
	//0 through 8 are numbers, -1 is a mine
	int[][] grid;
	//0 is hidden, 1 is already clicked on, 2 is flagged
	int[][] shown;
	RateLimiter sweeperLimiter;
	int numOfClicks;
	int x;
	int y;
	int timer;
	boolean lost;
	boolean showCursor;
	final int NUM_OF_MINES = 100;

	public Minesweeper() {
		reset();
		sweeperLimiter = new RateLimiter(0.25);
	}

	public void reset() {
		grid = new int[h][w];
		shown = new int[h][w];
		numOfClicks = 0;
		lost = false;
	}

	public void run(Context context) {
		while (true) {
			if (sweeperLimiter.next()) {
				log("Inside RateLimiter");
				showCursor = showCursor? false : true;
			}
			context.yield();
		}
	}

	public void moveRight() {
		if (x == w - 1) {
			x = 0;
		} else {
			x++;
		}
	}

	public void moveLeft() {
		if (x == 0) {
			x = w - 1;
		} else {
			x--;
		}
	}

	public void moveDown() {
		if (y == h - 1) {
			y = 0;
		} else {
			y++;
		}
	}

	public void moveUp() {
		if (y == 0) {
			y = h - 1;
		} else {
			y--;
		}
	}

	private void output(int i, int j) {
		if (lost) {
			Robot.candle.setColor(170, 170, 0, h * y + x + 8, w * h);
		}
		if (shown[i][j] == 0) {
			Robot.candle.setColor(170, 170, 170, h * i + j + 8, 1);
		} else if (shown[i][j] == 2) {
			Robot.candle.setColor(255, 0, 0, h * i + j + 8, 1);
		} else if (grid[i][j] == 0) {
			Robot.candle.setColor(0, 0, 0, h * i + j + 8, 1);
		} else if (grid[i][j] == -1) {
			//Yellow
			Robot.candle.setColor(170, 170, 0, h * i + j + 8, 1);
		} else if (grid[i][j] == 1) {
			//Blue
			Robot.candle.setColor(0, 0, 255, h * i + j + 8, 1);
		} else if (grid[i][j] == 2) {
			//Green
			Robot.candle.setColor(0, 145, 0, h * i + j + 8, 1);
		} else if (grid[i][j] == 3) {
			//Red-Orange
			Robot.candle.setColor(187, 51, 0, h * i + j + 8, 1);
		} else if (grid[i][j] == 4) {
			//Purple
			Robot.candle.setColor(94, 0, 170, h * i + j + 8, 1);
		} else if (grid[i][j] == 5) {
			//Plum
			Robot.candle.setColor(102, 0, 85, h * i + j + 8, 1);
		} else if (grid[i][j] == 6) {
			//Cyan
			Robot.candle.setColor(0, 128, 128, h * i + j + 8, 1);
		} else if (grid[i][j] == 7) {
			//Pink
			Robot.candle.setColor(255, 0, 85, h * i + j + 8, 1);
		} else if (grid[i][j] == 8) {
			//White
			Robot.candle.setColor(255, 255, 255, h * i + j + 8, 1);
		}
	}

	public void click() {
		if (numOfClicks == 0) {
			numOfClicks++;
			shown[y][x] = 1;
			output(y, x);
			if (y != 0) {
				shown[y - 1][x] = 1;
				output(y - 1, x);
				if (x != 0) {
					shown[y - 1][x - 1] = 1;
					output(y - 1, x - 1);
				}
				if (x != w - 1) {
					shown[y - 1][x + 1] = 1;
					output(y - 1, x + 1);
				}
			}
			if (x != 0) {
				shown[y][x - 1] = 1;
				output(y, x - 1);
			}
			if (x != w - 1) {
				shown[y][x + 1] = 1;
				output(y, x + 1);
			}
			if (y != h - 1) {
				shown[y + 1][x] = 1;
				if (x != 0) {
					shown[y + 1][x - 1] = 1;
					output(y + 1, x - 1);
				}
				if (x != w - 1) {
					shown[y + 1][x + 1] = 1;
					output(y + 1, x + 1);
				}
			}
			int num = NUM_OF_MINES;
			while (num > 0) {
				int placeY = (int) (Math.random() * h);
				int placeX = (int) (Math.random() * h);
				if (shown[placeY][placeX] == 0 && grid[y][x] != -1) {
					grid[y][x] = -1;
					num--;
				}
			}
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					if (grid[i][j] != -1) {
						if (y != 0) {
							if (grid[y - 1][x] == -1) {
								grid[i][j]++;
							}
							if (x != 0 && grid[y - 1][x - 1] == -1) {
								grid[i][j]++;
							}
							if (x != w - 1 && grid[y - 1][x + 1] == -1) {
								grid[i][j]++;
							}
						}
						if (x != 0 && grid[y][x - 1] == -1) {
							grid[i][j]++;
						}
						if (x != w - 1 && grid[y][x + 1] == -1) {
							grid[i][j]++;
						}
						if (y != h - 1) {
							if (grid[y + 1][x] == -1) {
								grid[i][j]++;
							}
							if (x != 0 && grid[y + 1][x - 1] == -1) {
								grid[i][j]++;
							}
							if (x != w - 1 && grid[y + 1][x + 1] == -1) {
								grid[i][j]++;
							}
						}
					}
				}
			}
			clearZeros(y - 1, x - 1);
			clearZeros(y - 1, x + 1);
			clearZeros(y + 1, x - 1);
			clearZeros(y + 1, x + 1);
		} else if (shown[y][x] == 0 && !lost) {
			numOfClicks++;
			clearZeros(y, x);
			if (grid[y][x] == -1) {
				lost = true;
			}
		}
	}

	public void flag() {
		if (shown[y][x] == 0) {
			shown[y][x] = 2;
		} else if (shown[y][x] == 2) {
			shown[y][x] = 0;
		}
	}

	private void clearZeros(int y, int x) {
		if (grid[y][x] == 0) {
			if (y != 0) {

				if (shown[y - 1][x] == 0) {
					shown[y - 1][x] = 1;
					output(y - 1, x);
					clearZeros(y - 1, x);
				}

				if (x != 0 && shown[y - 1][x - 1] == 0) {
					shown[y - 1][x - 1] = 1;
					output(y - 1, x - 1);
					clearZeros(y - 1, x - 1);
				}

				if (x != w - 1 && shown[y - 1][x + 1] == 0) {
					shown[y - 1][x + 1] = 1;
					output(y - 1, x + 1);
					clearZeros(y - 1, x + 1);
				}
			}

			if (x != 0 && shown[y][x - 1] == 0) {
				shown[y][x - 1] = 1;
				output(y, x - 1);
				clearZeros(y, x - 1);
			}

			if (x != w - 1 && shown[y][x + 1] == 0) {
				shown[y][x + 1] = 1;
				output(y, x + 1);
				clearZeros(y, x + 1);
			}

			if (y != h - 1) {
				if (shown[y + 1][x] == 0) {
					shown[y + 1][x] = 1;
					output(y + 1, x);
					clearZeros(y + 1, x);
				}
				if (x != 0 && shown[y + 1][x - 1] == 0) {
					shown[y + 1][x - 1] = 1;
					output(y + 1, x - 1);
					clearZeros(y + 1, x - 1);
				}
				if (x != w - 1 && shown[y + 1][x + 1] == 0) {
					shown[y + 1][x + 1] = 1;
					output(y + 1, x + 1);
					clearZeros(y + 1, x + 1);
				}
			}
		}
	}
}