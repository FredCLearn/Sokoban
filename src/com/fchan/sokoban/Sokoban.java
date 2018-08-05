package com.fchan.sokoban;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fchan.sokoban.LevelReader;

public class Sokoban extends JPanel {

	private LevelReader test;
	int timet = 0;
	int dx = 0;
	int dy = 0;
	Contents[][] tiles = null;
	ArrayList<Point> gp = new ArrayList<Point>();
	int steps = 0;

	private Rectangle2D.Double r1;
	boolean isReady = false;
	int sizec = 50;
	int levelset = 0;

	Sokoban(String fileName) {
		// Read File
		test = new LevelReader();
		test.readLevels(fileName);

		setFocusable(true);
		requestFocus();

		addKeyListener(new SokobanKeyListener());

		initLevel(levelset);
		setPreferredSize(new Dimension(tiles.length * 200,
				tiles[0].length * 100));

		FindPlayer();
		StoreGoals();

		// Timer

		Timer timer = new Timer();
		timer.schedule(new FredTimertask(), 0, 1000);

		// Changes isReady to True

		isReady = true;

	}

	void initLevel(int level) {
		tiles = new Contents[test.getWidth(level)][test.getHeight(level)];
		// Get contents of each cell
		for (int x = 0; x < test.getWidth(level); x++) {
			for (int y = 0; y < test.getHeight(level); y++) {
				tiles[x][y] = test.getTile(level, x, y);

			}
			FindPlayer();
			StoreGoals();
		}

	}

	public void paintComponent(Graphics g) {
		if (isReady == false) {
			return;
		}

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw Goals
		for (int alist = 0; alist < gp.size(); alist++) {
			Point test = gp.get(alist);
			r1 = new Rectangle2D.Double(test.getX() * sizec, test.getY()
					* sizec, sizec, sizec);
			g2.setStroke(new BasicStroke(3.0f));
			g2.setPaint(Color.CYAN);
			g2.fill(r1);
			g2.draw(r1);

		}

		// Draw Walls and Box
		for (int i = 0; i <= tiles.length - 1; i++) {
			for (int j = 0; j <= tiles[0].length - 1; j++) {

				if (tiles[i][j] == Contents.WALL) {
					r1 = new Rectangle2D.Double(i * sizec, j * sizec, sizec,
							sizec);
					g2.setStroke(new BasicStroke(3.0f));
					g2.setPaint(Color.GREEN);
					g2.fill(r1);
					g2.draw(r1);
				} else if (tiles[i][j] == Contents.BOX) {
					r1 = new Rectangle2D.Double(i * sizec + 5, j * sizec + 3,
							sizec - 7, sizec - 7);
					g2.setStroke(new BasicStroke(3.0f));
					g2.setPaint(Color.BLUE);
					g2.fill(r1);
					g2.draw(r1);
				}

			}

		}

		// Draw Player
		r1 = new Rectangle2D.Double(dx * sizec + 5, dy * sizec + 3, sizec - 7,
				sizec - 7);
		g2.setStroke(new BasicStroke(3.0f));
		g2.setPaint(Color.RED);
		g2.fill(r1);
		g2.draw(r1);
		
		
		//Draw Text
		String lvl = String.valueOf(levelset);
		g.drawString("Level: " + lvl, 400, 10);
		g.drawString("Press R to Restart Level", 400, 25);
		g.drawString("Press N for Next Level", 400, 40);
		String step2 = String.valueOf(steps);
		g.drawString("Steps Taken: " + step2, 400, 65);
		String time = String.valueOf(timet);
		g.drawString("Time: " + timet / 60 + ":" + timet % 60, 400, 90);
	}

	void FindPlayer() {

		for (int i = 0; i <= tiles.length - 1; i++) {
			for (int j = 0; j <= tiles[0].length - 1; j++) {
				if (tiles[i][j] == Contents.PLAYER) {
					dx = i;
					dy = j;
				}

			}

		}

	}

	void StoreGoals() {
		gp.clear();
		for (int i = 0; i <= tiles.length - 1; i++) {
			for (int j = 0; j <= tiles[0].length - 1; j++) {
				if (tiles[i][j] == Contents.GOAL) {
					gp.add(new Point(i, j));
				}
			}
		}
	}

	void NextLvl() {
		levelset = levelset + 1;
		initLevel(levelset);
		steps = 0;
		timet = 0;
		repaint();

	}

	private boolean checkWin() {
		for (int i = 0; i <= tiles.length - 1; i++) {
			for (int j = 0; j <= tiles[0].length - 1; j++) {

				if (tiles[i][j] == Contents.BOX) {
					boolean onGoal = false;
					for (int k = 0; k < gp.size(); k++) {
						Point p = gp.get(k);
						if (p.getX() == i && p.getY() == j) {
							onGoal = true;
							break;
						}
					}

					if (onGoal == false) {
						return false;
					}
				}
			}
		}
		return true;
	}

	boolean LookAhead(int xaheadby1, int yaheadby1, int xaheadby2, int yaheadby2) {
		if (tiles[xaheadby1][yaheadby1] == Contents.WALL) {
			return false;
		}
		// Moving Box

		if (tiles[xaheadby1][yaheadby1] == Contents.BOX) {
			if (tiles[xaheadby2][yaheadby2] == Contents.BOX) {
				return false;
			}
			if (tiles[xaheadby2][yaheadby2] == Contents.WALL) {
				return false;
			}
			tiles[xaheadby1][yaheadby1] = Contents.EMPTY;
			tiles[xaheadby2][yaheadby2] = Contents.BOX;
		}

		steps = steps + 1;
		return true;

	}

	// KeyListener inner class

	public class SokobanKeyListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {

			int c = e.getKeyCode();
			// Restart Level
			if (c == 82) {
				initLevel(levelset);
				steps = 0;
				timet = 0;

			}

			// Next Level
			if (c == 78) {
				NextLvl();
			}

			// Move Up
			if (c == 38) {
				if (LookAhead(dx, dy - 1, dx, dy - 2) == false) {
					return;
				}

				dy = dy - 1;
			}
			// Move Down
			if (c == 40) {
				if (LookAhead(dx, dy + 1, dx, dy + 2) == false) {
					return;
				}

				dy = dy + 1;
			}
			// Move Left
			if (c == 37) {
				if (LookAhead(dx - 1, dy, dx - 2, dy) == false) {
					return;
				}

				dx = dx - 1;
			}
			// Move Right
			if (c == 39) {
				if (LookAhead(dx + 1, dy, dx + 2, dy) == false) {
					return;
				}

				dx = dx + 1;
			}

			// Go to Next Level if Goals are met
			if (checkWin() == true) {
				NextLvl();

			}

			repaint();
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub

		}

	}

	// Timer Inner Class
	public class FredTimertask extends TimerTask {

		@Override
		public void run() {
			timet = timet + 1;
			repaint();
		}

	}
}
