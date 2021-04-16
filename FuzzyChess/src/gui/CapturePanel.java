package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

import models.ChessPiece;

public class CapturePanel extends JPanel {
	private static final long serialVersionUID = 2110193943756242580L;
	private GameResources resources;
	private JLabel titleLabel;
	private ImageGrid grid;
	private int WIDTH = 200;
	private int HEIGHT = 760;
	
	public CapturePanel(String title) {
		titleLabel = new JLabel(title);
		titleLabel.setHorizontalAlignment((int)JLabel.CENTER_ALIGNMENT);
		grid = new ImageGrid(8,2);
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		setLayout(new BorderLayout());
		add(titleLabel, BorderLayout.NORTH);
		add(grid, BorderLayout.CENTER);
	}
	
	private class ImageGrid extends JPanel{
		private static final long serialVersionUID = -1520709109005506504L;
		private int gridOffset = 20;
		private int spaceSize = 80;
		private int spaceOffset = 5;
		private int rows;
		private int cols;
		private ArrayList<BufferedImage> images;
		
		public ImageGrid(int r, int c) {
			//setPreferredSize(new Dimension(WIDTH,HEIGHT));
			images = new ArrayList<BufferedImage>();
			rows = r;
			cols = c;
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			setBackground(resources.getBackgroundColor());
			g.setColor(resources.getBoardColor());
			g.fillRect(gridOffset, 0, spaceSize*cols, spaceSize*rows);
			g.setColor(resources.getBoardBorderColor());
			int pieceIndex = 0;
			for(int i = 0; i < rows; i++) {
				for(int j = 0; j < cols; j++) {
					if(pieceIndex < images.size()) {
						g.drawImage(images.get(pieceIndex++), j * spaceSize+spaceOffset+gridOffset, i * spaceSize+spaceOffset,
								spaceSize-(spaceOffset*2), spaceSize-(spaceOffset*2), resources.getBoardColor(), this);
					}
					g.drawRect(j*spaceSize+gridOffset, i*spaceSize, spaceSize, spaceSize);
				}
			}
			
			g.dispose();
		}
		
		public void setImages(ArrayList<BufferedImage> imgs) {
			images = imgs;
		}
	}
	
	public void setTheme(GameResources t) {
		resources = t;
		updateFont();
		updateColors();
	}
	
	public void update(ArrayList<ChessPiece> pieces) {
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		for(ChessPiece piece : pieces) {
			images.add(resources.getChessSprite(piece.getid()));
		}
		grid.setImages(images);
		repaint();
	}
	
	public void updateFont() {
		titleLabel.setFont(resources.getFontStyle());
	}
	
	public void updateColors() {
		titleLabel.setForeground(resources.getForegroundColor());
		setBackground(resources.getBackgroundColor());
		setBorder(BorderFactory.createLineBorder(resources.getForegroundColor()));
	}
}
