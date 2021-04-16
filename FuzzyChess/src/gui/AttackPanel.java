package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import engine.FuzzyChessEngine;

public class AttackPanel extends JPanel implements Runnable{
	private static final long serialVersionUID = 1030010850185263656L;
	private GameResources resources;
	private ImagePanel attackerPanel;
	private ImagePanel dicePanel;
	private ImagePanel defenderPanel;
	private JLabel attackerLabel;
	private JLabel defenderLabel;
	private JLabel rollsNeededLabel;
	private Thread diceRoller;
	private Timer thinkingAnimator;
	private String captureResult;
	private int lastRoll;
	//callback to engine to update game after dice roll animation
	private FuzzyChessEngine callback;
	
	public AttackPanel() {
		setLayout(new BorderLayout());
		JPanel p = new JPanel();
		p.setBackground(Color.DARK_GRAY);
		p.setLayout(new FlowLayout());
		
		//setLayout(new FlowLayout());
		rollsNeededLabel = new JLabel(" ");
		rollsNeededLabel.setHorizontalAlignment(JLabel.CENTER);
		attackerLabel = new JLabel("Attacker");
		defenderLabel = new JLabel("Defender");
		attackerPanel = new ImagePanel();
		dicePanel = new ImagePanel();
		defenderPanel = new ImagePanel();
		
		p.add(attackerLabel);
		p.add(attackerPanel);
		p.add(dicePanel);
		p.add(defenderPanel);
		p.add(defenderLabel);
		
		add(p, BorderLayout.CENTER);
		add(rollsNeededLabel, BorderLayout.SOUTH);
	}
	
	private class ImagePanel extends JPanel{
		private static final long serialVersionUID = 3005984523390317987L;
		private int WIDTH = 100;
		private int HEIGHT = 100;
		private int OFFSET = 10;
		private BufferedImage img;
		
		public ImagePanel() {
			setPreferredSize(new Dimension(WIDTH,HEIGHT));
			setLayout(new BorderLayout());
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			setBackground(resources.getBoardColor());
			g.setColor(resources.getBoardBorderColor());
			g.drawRect(0,0,WIDTH-1,HEIGHT-1);
			
			if(img != null) {
				g.drawImage(img, OFFSET, OFFSET, WIDTH-OFFSET*2, HEIGHT-OFFSET*2, resources.getBoardColor(), this);
			}
			g.dispose();
		}
		
		public void setImage(BufferedImage i) {
			img = i;
		}
		
	}
	
	//start animation - freeze rest of gui
	public void rollDice(int lastroll, String rollsNeeded, String captureResult) {
		this.lastRoll = lastroll;
		this.captureResult = captureResult;
		rollsNeededLabel.setText(rollsNeeded);
		if(diceRoller == null) {
			diceRoller = new Thread(this);
			diceRoller.start();
		}
	}
	
	public void startThinkingAnimation() {
		if(thinkingAnimator == null) {
			thinkingAnimator = new Timer(500, new ActionListener() {
				int dotCount = 0;
				@Override
				public void actionPerformed(ActionEvent e) {
					dotCount = ++dotCount % 4;
					String dots = "";
					for(int i = 0; i < dotCount; i++) {
						dots += ".";
					}
					setText("The enemy commanders are plotting your demise" + dots);
				}
			});
			thinkingAnimator.start();
		}
	}
	
	public void stopThinkingAnimation() {
		if(thinkingAnimator != null) {
			thinkingAnimator.stop();
			thinkingAnimator = null;
		}
	}
	
	public void update(char attackerID, char defenderID) {
		attackerPanel.setImage(resources.getChessSprite(attackerID));
		defenderPanel.setImage(resources.getChessSprite(defenderID));
		rollsNeededLabel.setText(" ");
		attackerPanel.repaint();
		defenderPanel.repaint();
	}
	
	public void setTheme(GameResources t) {
		resources = t;
		setBackground(resources.getBackgroundColor());
		rollsNeededLabel.setBackground(resources.getBackgroundColor());
		rollsNeededLabel.setForeground(resources.getForegroundColor());
		rollsNeededLabel.setFont(resources.getFontStyle());
		attackerLabel.setForeground(resources.getForegroundColor());
		defenderLabel.setForeground(resources.getForegroundColor());
		setBorder(BorderFactory.createLineBorder(resources.getBoardBorderColor()));
		attackerLabel.setFont(resources.getFontStyle());
		defenderLabel.setFont(resources.getFontStyle());
		dicePanel.setImage(resources.getDiceSprite(1));
		attackerPanel.repaint();
		defenderPanel.repaint();
		dicePanel.repaint();
	}

	@Override
	public void run() {
		int frames = 15;
		int lastImageID = 0;
		for(int i = 1; i <= frames; i++) {
			int nextImageID = i < frames ? (int)((Math.random() * 100) % 6) + 1 : lastRoll;
			lastImageID = nextImageID;
			while(lastImageID == nextImageID && i < frames) {
				nextImageID = i < frames ? (int)((Math.random() * 100) % 6) + 1 : lastRoll;
			}
			dicePanel.setImage(resources.getDiceSprite(nextImageID));
			dicePanel.repaint();
			try {
				Thread.sleep(200);
			} catch(InterruptedException e) {}
		}
		try {
			//show result for small amt of time then return control
			rollsNeededLabel.setText(captureResult);
			Thread.sleep(1000);
		} catch(InterruptedException e) {}
		diceRoller = null;
		callback.callbackUpdate();
	}
	
	public void setCallBackRef(FuzzyChessEngine engine) {
		callback = engine;
	}
	
	public void setText(String text) {
		rollsNeededLabel.setText(text);
	}

	public Timer getThinkingAnimator(){
		return thinkingAnimator;
	}
}
