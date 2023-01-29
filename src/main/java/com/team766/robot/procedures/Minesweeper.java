package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;
import edu.wpi.first.math.Pair;
import com.team766.library.RateLimiter;

public class Minesweeper extends Procedure {

	private int w = 22;
    private int h = 22;
	//0 through 8 are numbers, -1 is a mine
	int[][] grid;
	//0 is hidden, 1 is already clicked on, 2 is flagged
	int[][] shown;
	private RateLimiter sweeperLimiter;
	int numOfClicks;
	int x;
	int y;
	int timer;

	public Minesweeper() {
		grid = new int[h][w];
		shown = new int[h][w];
		sweeperLimiter = new RateLimiter(0.25);
	}

	public void run(Context context) {
		while (true) {
			if (sweeperLimiter.next()) {
				log("Inside RateLimiter");
				context.takeOwnership(Robot.candle);
				output();
				context.releaseOwnership(Robot.candle);
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

	private void output() {
		timer = (timer + 1) % 2;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (shown[i][j] == 0) {
			  		Robot.candle.setColor(0.2, 0.2, 0.2, h * i + j + 8, 1);
				} else if (shown[i][j] == 2) {
					Robot.candle.setColor(0.3, 0, 0, h * i + j + 8, 1);
				} else if (grid[i][j] == 0) {
					Robot.candle.setColor(0, 0, 0, h * i + j + 8, 1);
				} else if (grid[i][j] == 1) {
					//Yellow
					Robot.candle.setColor(0.2, 0.2, 0, h * i + j + 8, 1);
				} else if (grid[i][j] == 1) {
					//Blue
					Robot.candle.setColor(0, 0, 0.3, h * i + j + 8, 1);
				} else if (grid[i][j] == 2) {
					//Green
					Robot.candle.setColor(0, 0.17, 0, h * i + j + 8, 1);
				} else if (grid[i][j] == 3) {
					//Red-Orange
					Robot.candle.setColor(0.22, 0.06, 0, h * i + j + 8, 1);
				} else if (grid[i][j] == 4) {
					//Purple
					Robot.candle.setColor(0.11, 0, 0.2, h * i + j + 8, 1);
				} else if (grid[i][j] == 5) {
					//Plum
					Robot.candle.setColor(0.12, 0, 0.1, h * i + j + 8, 1);
				} else if (grid[i][j] == 6) {
					//Cyan
					Robot.candle.setColor(0, 0.15, 0.15, h * i + j + 8, 1);
				} else if (grid[i][j] == 7) {
					//Pink
					Robot.candle.setColor(0.3, 0, 0.10, h * i + j + 8, 1);
				} else if (grid[i][j] == 8) {
					//White
					Robot.candle.setColor(0.3, 0.3, 0.3, h * i + j + 8, 1);
				}
			}
		}
		if (timer == 0) {
			Robot.candle.setColor(0.3, 0.3, 0.3, h * y + x + 8, 1);
		}
	}

	public void click() {
		if (numOfClicks == 0) {
			numOfClicks++;
			shown[y][x] = 1;
			if (y != 0) {
				shown[y - 1][x] = 1;
				if (x != 0) {
					shown[y - 1][x - 1] = 1;
				}
				if (x != w - 1) {
					shown[y - 1][x + 1] = 1;
				}
			}
			if (x != 0) {
				shown[y][x - 1] = 1;
			}
			if (x != w - 1) {
				shown[y][x + 1] = 1;
			}
			if (y != h - 1) {
				shown[y + 1][x] = 1;
				if (x != 0) {
					shown[y + 1][x - 1] = 1;
				}
				if (x != w - 1) {
					shown[y + 1][x + 1] = 1;
				}
			}
			int num = 100;
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
			clearZeros(y, x);
		} else if (shown[y][x] == 0) {
			numOfClicks++;
			clearZeros(y, x);
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
				shown[y - 1][x] = 1;
				clearZeros(y - 1, x);
				if (x != 0) {
					shown[y - 1][x - 1] = 1;
					clearZeros(y - 1, x - 1);
				}
				if (x != w - 1) {
					shown[y - 1][x + 1] = 1;
					clearZeros(y - 1, x + 1);
				}
			}
			if (x != 0) {
				shown[y][x - 1] = 1;
				clearZeros(y, x - 1);
			}
			if (x != w - 1) {
				shown[y][x + 1] = 1;
				clearZeros(y, x + 1);
			}
			if (y != h - 1) {
				shown[y + 1][x] = 1;
				clearZeros(y + 1, x);
				if (x != 0) {
					shown[y + 1][x - 1] = 1;
					clearZeros(y + 1, x - 1);
				}
				if (x != w - 1) {
					shown[y + 1][x + 1] = 1;
					clearZeros(y + 1, x + 1);
				}
			}
		}
	}
}