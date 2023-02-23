package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;
import edu.wpi.first.math.Pair;
import com.team766.library.RateLimiter;

public class Minesweeper extends Procedure {

	private int w = 16;
	private int h = 16;
	// 0 through 8 are numbers, -1 is a mine
	int[][] grid;
	// 0 is hidden, 1 is already clicked on, 2 is flagged
	int[][] shown;
	RateLimiter sweeperLimiter;
	int numOfClicks;
	int x;
	int y;
	boolean lost;
	boolean won;
	boolean showCursor;
	Context mineContext;
	final int NUM_OF_MINES = 53;

	public boolean isClicking = false;
	public boolean isRight = false;
	public boolean isLeft = false;
	public boolean isUp = false;
	public boolean isDown = false;
	public boolean isUpRight = false;
	public boolean isDownLeft = false;
	public boolean isUpLeft = false;
	public boolean isDownRight = false;
	public boolean isFlagging = false;
	public boolean isResetting = false;

	boolean isVisible = true;
	boolean justShown = false;

	public Minesweeper() {
		reset();
		sweeperLimiter = new RateLimiter(0.25);
	}

	public void changeVisible() {
		isVisible = !isVisible;
		if (isVisible) {
			justShown = true;
		}
	}

	public void reset() {
		grid = new int[h][w];
		shown = new int[h][w];
		numOfClicks = 0;
		lost = false;
		won = false;
		if (mineContext != null) {
			mineContext.takeOwnership(Robot.candle);
			Robot.candle.setColor(30, 30, 30);
			mineContext.releaseOwnership(Robot.candle);
		}
	}

	public void run(Context context) {
		mineContext = context;
		context.takeOwnership(Robot.candle);
		Robot.candle.setColor(30, 30, 30);
		context.releaseOwnership(Robot.candle);
		while (true) {
			mineContext = context;
			if (justShown) {
				for (int i = 0; i < h; i += 2) {
					for (int j = 0; j < w; j++) {
						output(i, j);
					}
					context.waitForSeconds(0.01);
				}
				for (int i = 1; i < h; i += 2) {
					for (int j = 0; j < w; j++) {
						output(i, j);
					}
					context.waitForSeconds(0.01);
				}
			}
			if (sweeperLimiter.next()) {
				showCursor = !showCursor;
				output(y, x);
			}
			if (isClicking) {
				click();
				isClicking = false;
				log("Click");
			}
			if (isRight) {
				moveRight();
				isRight = false;
				log("Right");
			}
			if (isUp) {
				moveUp();
				isUp = false;
				log("Up");
			}
			if (isDown) {
				moveDown();
				isDown = false;
				log("Down");
			}
			if (isLeft) {
				moveLeft();
				isLeft = false;
				log("Left");
			}
			if (isUpRight) {
				moveUpRight();
				isUpRight = false;
				log("Up Right");
			}
			if (isDownLeft) {
				moveDownLeft();
				isDownLeft = false;
				log("Down Left");
			}
			if (isUpLeft) {
				moveUpLeft();
				isUpLeft = false;
				log("Up Left");
			}
			if (isDownRight) {
				moveDownRight();
				isDownRight = false;
				log("Down Right");
			}
			if (isFlagging) {
				flag();
				isFlagging = false;
				log("Flag");
			}
			if (isResetting) {
				reset();
				isResetting = false;
				log("Reset");
			}
			checkForWin();
			context.yield();
		}
	}

	public void moveRight() {
		if (x == w - 1) {
			x = 0;
			output(y, w - 1);
		} else {
			x++;
			output(y, x - 1);
		}
		output(y, x);
	}

	public void moveLeft() {
		if (x == 0) {
			x = w - 1;
			output(y, 0);
		} else {
			x--;
			output(y, x + 1);
		}
		output(y, x);
	}

	public void moveDown() {
		if (y == h - 1) {
			y = 0;
			output(h - 1, x);
		} else {
			y++;
			output(y - 1, x);
		}
		output(y, x);
	}

	public void moveUp() {
		if (y == 0) {
			y = h - 1;
			output(0, x);
		} else {
			y--;
			output(y + 1, x);
		}
		output(y, x);
	}

	public void moveUpRight() {
		moveUp();
		moveRight();
	}

	public void moveDownLeft() {
		moveDown();
		moveLeft();
	}

	public void moveDownRight() {
		moveDown();
		moveRight();
	}

	public void moveUpLeft() {
		moveUp();
		moveLeft();
	}

	private void output(int i, int j) {
		if (isVisible) {
			mineContext.takeOwnership(Robot.candle);
			if (lost) {
				Robot.candle.setColor(255, 0, 0);
			} else if (won) {
				Robot.candle.setColor(0, 255, 0);
			} else if (showCursor && i == y && j == x) {
				Robot.candle.setColor(255, 255, 255, Robot.candle.getMatrixID(i, j), 1);
			} else if (shown[i][j] == 0) {
				Robot.candle.setColor(30, 30, 30, Robot.candle.getMatrixID(i, j), 1);
			} else if (shown[i][j] == 2) {
				Robot.candle.setColor(255, 0, 0, Robot.candle.getMatrixID(i, j), 1);
			} else if (grid[i][j] == 0) {
				Robot.candle.setColor(0, 0, 0, Robot.candle.getMatrixID(i, j), 1);
			} else if (grid[i][j] == 1) {
				// Blue
				Robot.candle.setColor(0, 0, 255, Robot.candle.getMatrixID(i, j), 1);
			} else if (grid[i][j] == 2) {
				// Green
				Robot.candle.setColor(0, 145, 0, Robot.candle.getMatrixID(i, j), 1);
			} else if (grid[i][j] == 3) {
				// Red-Orange
				Robot.candle.setColor(187, 51, 0, Robot.candle.getMatrixID(i, j), 1);
			} else if (grid[i][j] == 4) {
				// Purple
				Robot.candle.setColor(94, 0, 170, Robot.candle.getMatrixID(i, j), 1);
			} else if (grid[i][j] == 5) {
				// Yellow
				Robot.candle.setColor(170, 170, 0, Robot.candle.getMatrixID(i, j), 1);
			} else if (grid[i][j] == 6) {
				// Cyan
				Robot.candle.setColor(0, 128, 128, Robot.candle.getMatrixID(i, j), 1);
			} else if (grid[i][j] == 7) {
				// Pink
				Robot.candle.setColor(255, 0, 85, Robot.candle.getMatrixID(i, j), 1);
			} else if (grid[i][j] == 8) {
				// White
				Robot.candle.setColor(255, 255, 255, Robot.candle.getMatrixID(i, j), 1);
			}
			mineContext.releaseOwnership(Robot.candle);
		}
	}

	public void checkForWin() {
		boolean winning = true;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (grid[i][j] != -1 && shown[i][j] != 1) {
					winning = false;
				}
			}
		}
		if (winning)
			won = true;
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
			int num = NUM_OF_MINES;
			while (num > 0) {
				int placeY = (int) (Math.random() * h);
				int placeX = (int) (Math.random() * w);
				if (shown[placeY][placeX] == 0 && grid[placeY][placeX] != -1) {
					grid[placeY][placeX] = -1;
					num--;
					log("" + num + " " + x + " " + y);
				}
			}
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					if (grid[i][j] != -1) {
						if (i != 0) {
							if (grid[i - 1][j] == -1) {
								grid[i][j]++;
							}
							if (j != 0 && grid[i - 1][j - 1] == -1) {
								grid[i][j]++;
							}
							if (j != w - 1 && grid[i - 1][j + 1] == -1) {
								grid[i][j]++;
							}
						}
						if (j != 0 && grid[i][j - 1] == -1) {
							grid[i][j]++;
						}
						if (j != w - 1 && grid[i][j + 1] == -1) {
							grid[i][j]++;
						}
						if (i != h - 1) {
							if (grid[i + 1][j] == -1) {
								grid[i][j]++;
							}
							if (j != 0 && grid[i + 1][j - 1] == -1) {
								grid[i][j]++;
							}
							if (j != w - 1 && grid[i + 1][j + 1] == -1) {
								grid[i][j]++;
							}
						}
					}
				}
			}
			if (y != 0) {
				output(y - 1, x);
				clearZeros(y - 1, x);
				if (x != 0) {
					output(y - 1, x - 1);
					clearZeros(y - 1, x - 1);
				}
				if (x != w - 1) {
					output(y - 1, x + 1);
					clearZeros(y - 1, x + 1);
				}
			}
			if (x != 0) {
				output(y, x - 1);
				clearZeros(y, x - 1);
			}
			if (x != w - 1) {
				output(y, x + 1);
				clearZeros(y, x + 1);
			}
			if (y != h - 1) {
				output(y + 1, x);
				clearZeros(y + 1, x);
				if (x != 0) {
					output(y + 1, x - 1);
					clearZeros(y + 1, x - 1);
				}
				if (x != w - 1) {
					output(y + 1, x + 1);
					clearZeros(y + 1, x + 1);
				}
			}
		} else if (shown[y][x] == 0 && !lost && !won) {
			numOfClicks++;
			shown[y][x] = 1;
			clearZeros(y, x);
			output(y, x);
			if (grid[y][x] == -1) {
				lost = true;
			}
		} else if (shown[y][x] == 1 && !lost && !won) {
			int numAround = 0;
			if (y != 0) {
				numAround += shown[y - 1][x] == 2 ? 1 : 0;
				if (x != 0) {
					numAround += shown[y - 1][x - 1] == 2 ? 1 : 0;
				}
				if (x != w - 1) {
					numAround += shown[y - 1][x + 1] == 2 ? 1 : 0;
				}
			}
			if (x != 0) {
				numAround += shown[y][x - 1] == 2 ? 1 : 0;
			}
			if (x != w - 1) {
				numAround += shown[y][x + 1] == 2 ? 1 : 0;
			}
			if (y != h - 1) {
				numAround += shown[y + 1][x] == 2 ? 1 : 0;
				if (x != 0) {
					numAround += shown[y - 1][x - 1] == 2 ? 1 : 0;
				}
				if (x != w - 1) {
					numAround += shown[y + 1][x + 1] == 2 ? 1 : 0;
				}
			}
			if (numAround == grid[x][y]) {
				numOfClicks++;
				if (y != 0) {
					moveUp();
					if (shown[y][x] == 0 && !lost && !won) {
						click();
					}
					moveDown();
					if (x != 0) {
						moveUpLeft();
						if (shown[y][x] == 0 && !lost && !won) {
							click();
						}
						moveDownRight();
					}
					if (x != w - 1) {
						moveUpRight();
						if (shown[y][x] == 0 && !lost && !won) {
							click();
						}
						moveDownLeft();
					}
				}
				if (x != 0) {
					moveLeft();
					if (shown[y][x] == 0 && !lost && !won) {
						click();
					}
					moveRight();
				}
				if (x != w - 1) {
					moveRight();
					if (shown[y][x] == 0 && !lost && !won) {
						click();
					}
					moveLeft();
				}
				if (y != h - 1) {
					moveDown();
					if (shown[y][x] == 0 && !lost && !won) {
						click();
					}
					moveUp();
					if (x != 0) {
						moveDownLeft();
						if (shown[y][x] == 0 && !lost && !won) {
							click();
						}
						moveUpRight();
					}
					if (x != w - 1) {
						moveDownRight();
						if (shown[y][x] == 0 && !lost && !won) {
							click();
						}
						moveUpLeft();
					}
				}
			}
		}
	}

	public void flag() {
		if (numOfClicks > 0 && !won && !lost) {
			if (shown[y][x] == 0) {
				shown[y][x] = 2;
			} else if (shown[y][x] == 2) {
				shown[y][x] = 0;
			} else {
				int numAround = 0;
				if (y != 0) {
					numAround += shown[y - 1][x] == 1 ? 0 : 1;
					if (x != 0) {
						numAround += shown[y - 1][x - 1] == 1 ? 0 : 1;
					}
					if (x != w - 1) {
						numAround += shown[y - 1][x + 1] == 1 ? 0 : 1;
					}
				}
				if (x != 0) {
					numAround += shown[y][x - 1] == 1 ? 0 : 1;
				}
				if (x != w - 1) {
					numAround += shown[y][x + 1] == 1 ? 0 : 1;
				}
				if (y != h - 1) {
					numAround += shown[y + 1][x] == 1 ? 0 : 1;
					if (x != 0) {
						numAround += shown[y - 1][x - 1] == 1 ? 0 : 1;
					}
					if (x != w - 1) {
						numAround += shown[y + 1][x + 1] == 1 ? 0 : 1;
					}
				}
				if (numAround == grid[x][y]) {
					if (y != 0) {
						moveUp();
						if (shown[y][x] == 0 && !lost && !won) {
							flag();
						}
						moveDown();
						if (x != 0) {
							moveUpLeft();
							if (shown[y][x] == 0 && !lost && !won) {
								flag();
							}
							moveDownRight();
						}
						if (x != w - 1) {
							moveUpRight();
							if (shown[y][x] == 0 && !lost && !won) {
								flag();
							}
							moveDownLeft();
						}
					}
					if (x != 0) {
						moveLeft();
						if (shown[y][x] == 0 && !lost && !won) {
							flag();
						}
						moveRight();
					}
					if (x != w - 1) {
						moveRight();
						if (shown[y][x] == 0 && !lost && !won) {
							flag();
						}
						moveLeft();
					}
					if (y != h - 1) {
						moveDown();
						if (shown[y][x] == 0 && !lost && !won) {
							flag();
						}
						moveUp();
						if (x != 0) {
							moveDownLeft();
							if (shown[y][x] == 0 && !lost && !won) {
								flag();
							}
							moveUpRight();
						}
						if (x != w - 1) {
							moveDownRight();
							if (shown[y][x] == 0 && !lost && !won) {
								flag();
							}
							moveUpLeft();
						}
					}
				}
			}
			output(y, x);
		}
	}

	private void clearZeros(int y, int x) {
		log("Clearing " + x + " " + y + " " + grid[y][x]);
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
