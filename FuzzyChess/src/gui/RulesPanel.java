package gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

public class RulesPanel extends JPanel implements ActionListener {
	String rules[] = {"HOW TO MOVE: Your army consists of three commanders, your two bishops, and the king.",
					  "Each turn your commanders can command the forces indicated by a green square - click these.",
					  "You can move to any space indicated by a blue square - or to one indicated by a red square.",
					  "",
					  "HOW TO ATTACK: When you click a square that is red you start battle with that piece",
					  "A die - shown at the top of the screen is rolled and if you roll well enough as shown below you win.",
					  "",
					  "Fight hard - and capture your enemies commanders and if you capture the king - they will surrender",
					  "                                                    GOOD LUCK!"};
	
	String[] chart = {"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
			          "|  Attacker |                            Defender                       |",
			          "|               |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|",
			          "|               |    KING    |   QUEEN   |   KNIGHT   |  ROOK   |  BISHOP  |   PAWN   |",
			          "|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|",
			          "|    KING     |    6,5,4   |   6,5,4   |   6,5,4    |   6,5   |   6,5,4  | automatic|",
			          "|    QUEEN    |    6,5,4   |   6,5,4   |   6,5,4    |   6,5   |   6,5,4  | 6,5,4,3,2|",
			          "|    KNIGHT   |      6     |     6     |   6,5,4    |   6,5   |   6,5,4  | 6,5,4,3,2|",
			          "|    BISHOP   |     6,5    |    6,5    |    6,5     |   6,5   |   6,5,4  | 6,5,4,3  |",
			          "|    ROOK     |    6,5,4   |   6,5,4   |    6,5     |    6    |    6,5   |   6,5    |",
			          "|    PAWN     |      6     |     6     |     6      |    6    |    6,5   |  6,5,4   |",
			          "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"};
	GameResources resources;
	JButton goBack;
	ArrayList<BufferedImage> images;
	FuzzyChessDisplay display;
	
	public RulesPanel(FuzzyChessDisplay display) {
		setVisible(true);
		setLayout(new BorderLayout());
		RulesDisplay rulesDisplay = new RulesDisplay();
		goBack = new JButton("OK");
		goBack.addActionListener(this);
		
		add(rulesDisplay, BorderLayout.CENTER);
		add(goBack, BorderLayout.SOUTH);
		this.display = display;
	}
	
	
	private class RulesDisplay extends JPanel{
		int WIDTH = 900;
		int HEIGHT = 900;
		public RulesDisplay() {
			setPreferredSize(new Dimension(WIDTH,HEIGHT));
		}
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			setBackground(resources.getBackgroundColor());
			g.setColor(resources.getBackgroundColor());
			g.fillRect(0, 0, WIDTH, HEIGHT);
			g.setColor(resources.getBoardBackgroundColor());
			g.fillRect(20, 20, WIDTH-40, 240);
			g.setColor(resources.getForegroundColor());
			g.drawRect(20, 20, WIDTH-40, 240);
			
			g.setFont(resources.getFontStyle());
			g.drawString("HOW TO PLAY", WIDTH/2 - 100, 40);
			
			g.setFont(new Font("TimesRoman", Font.PLAIN, 18));
			
			drawChart(g, 20, 280, WIDTH-40, 400);
			drawText(g, 25, 80);
			
			g.dispose();
		}
		
		public void drawText(Graphics g, int x, int y) {
			for(int i = 0; i < rules.length; i++) {
				g.drawString(rules[i], 20, i * 20 + y);
			}
		}
		
		public void drawChart(Graphics g, int x, int y, int w, int h) {
			g.setColor(resources.getBoardBackgroundColor());
			g.fillRect(x, y, w, h);
			g.setColor(resources.getForegroundColor());
			
			//horizontals
			g.drawLine(x, y, (x+w), y);
			g.drawLine(x+(w/7), y+(h/8), x+w, y+(h/8));
			g.drawLine(x, y+(h/4), x+w, y+(h/4));
			g.drawLine(x, y+h, x+w, y+h);
			
			//verticals
			g.drawLine(x, y, x, (y+h));
			g.drawLine(x+w, y, x+w, (y+h));
			for(int i = 1; i <= 7; i++) {
				if(i == 1) {
					g.drawLine(x+(i*w/7), y, x+(i*w/7), y+h);
				}
				g.drawLine(x+(i*w/7), y+(h/8), x+(i*w/7), y+h);
			}
			
			//text
			g.drawString("Attacker", x+20, y+40);
			g.drawString("Defender", x+(w/2), y+40);
			String[] labels = {"KING", "QUEEN", "KNIGHT", "ROOK", "BISHOP", "PAWN"};
			String[][] data = {{"6,5,4", "6,5,4", "6,5,4", "6,5", "6,5,4", "automatic"},
							   {"6,5,4", "6,5,4", "6,5,4", "6,5", "6,5,4", "6,5,4,3,2"},
							   {"6","6","6,5,4","6,5","6,5,4","6,5,4,3"},
							   {"6,5,4","6,5,4","6,5","6","6,5","6,5"},
							   {"6,5","6,5","6,5","6,5","6,5,4","6,5,4,3"},
							   {"6","6","6","6","6,5","6,5,4"}};
			
			for(int i = 1; i <= labels.length; i++) {
				g.drawString(labels[i-1], x+(i*w/7)+10, y+2*h/8-10);
				g.drawString(labels[i-1], x+20, y+((i+2)*h/8-10));
			}
			
			for(int i = 1; i <= data.length; i++) {
				for(int j = 1; j <= data[0].length; j++) {
					g.drawString(data[i-1][j-1], x+(j*w/7)+10, y+((i+2)*h/8-10));
				}
			}
			
		}
	}
	
	public void setResources(GameResources g) {
		resources = g;
		images = resources.getRulesImages();
		goBack.setForeground(resources.getForegroundColor());
		goBack.setFont(resources.getFontStyle());
		goBack.setBackground(resources.getBoardBackgroundColor());
		repaint();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		display.reset();
	}
}
