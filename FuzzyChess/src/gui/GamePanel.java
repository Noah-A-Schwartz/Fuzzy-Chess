package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.swing.*;


public class GamePanel extends JPanel {
	private static final long serialVersionUID = -3107018262033523533L;
	private final int WIDTH = 760;
	private final int HEIGHT = 760;
	private final int OFFSET = 60;
	private final int SPACE_SIZE = 80;
	
	private char[][] boardState;
	private char[][] boardColors;
	
	private GameResources resources;
	
	public GamePanel() {
		boardState = new char[8][8];
		boardColors = new char[8][8];
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(resources.getBoardBackgroundColor());
		drawBorder(g);
		drawBoard(g);
		g.dispose();
	}
	
	private void drawBoard(Graphics g) {
		for(int i = 0; i < boardState.length; i++) {
			for(int j = 0; j < boardState.length; j++) {
				BufferedImage img = resources.getChessSprite(boardState[i][j]);
				int x = j * SPACE_SIZE + OFFSET;
				int y = i * SPACE_SIZE + OFFSET;
				int width = SPACE_SIZE;
				int height = SPACE_SIZE;
				Color spaceColor = resources.getSpaceColor(boardColors[i][j]);
				
				g.setColor(spaceColor);
				g.fillRect(x, y, width, height);
				//g.drawImage(img, x, y, spaceColor, this);
				g.drawImage(img, x, y, width, height, spaceColor, null);
				
				g.setColor(resources.getBoardBorderColor());
				g.drawRect(x, y, width, height);
			}
		}
	}
	
	private void drawBorder(Graphics g) {
		g.setColor(resources.getBoardBorderColor());
		g.drawRect(0, 0, WIDTH-1, HEIGHT-1);
		g.drawLine(0, 0, OFFSET, OFFSET);
		g.drawLine(WIDTH-OFFSET, OFFSET, WIDTH, 0);
		g.drawLine(0, HEIGHT, OFFSET, HEIGHT-OFFSET);
		g.drawLine(WIDTH, HEIGHT, WIDTH-OFFSET, HEIGHT-OFFSET);
	}
	
	public void updateBoard(char[][] board_state, char[][] board_colors) {
		for(int i = 0; i < board_state.length; i++) {
			for(int j = 0; j < board_state.length; j++) {
				boardState[i][j] = board_state[i][j];
				boardColors[i][j] = board_colors[i][j];
			}
		}
		repaint();
	}
	
	public void setTheme(GameResources t) {
		resources = t;
		repaint();
	}
}
