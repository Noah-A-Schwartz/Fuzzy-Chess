package gui;
import java.awt.*;

import javax.swing.*;

import fireworks.FireworksPanel;

public class FuzzyChessDisplay {
	private GameResources resources;
	private JFrame display;
	private StatusPanel statusPanel;
	private CapturePanel capturePanel1;
	private CapturePanel capturePanel2;
	private GamePanel gamePanel;
	private AttackPanel attackPanel;
	private JPanel winScreen;
	private RulesPanel helpScreen;

	private JMenuItem howToPlayMenuItem;
	private JCheckBoxMenuItem devModeMenuItem;
	private JMenuItem hardMenuItem;
	private JMenuItem medMenuItem;
	private JMenuItem easyMenuItem;
	private JMenuItem veryEasyMenuItem;

	
	public FuzzyChessDisplay() {
		display = new JFrame();
		display.setTitle("Medieval Warfare");
		display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		display.getContentPane().setLayout(new BorderLayout());
		display.setVisible(true);
		
		initMenu();		
		statusPanel = new StatusPanel();
		capturePanel1 = new CapturePanel("White Captures");
		capturePanel2 = new CapturePanel("Black Captures");
		gamePanel = new GamePanel();
		attackPanel = new AttackPanel();
		
		display.getContentPane().add(statusPanel, BorderLayout.SOUTH);
		display.getContentPane().add(capturePanel1, BorderLayout.WEST);
		display.getContentPane().add(capturePanel2, BorderLayout.EAST);
		display.getContentPane().add(gamePanel, BorderLayout.CENTER);
		display.getContentPane().add(attackPanel, BorderLayout.NORTH);
		display.validate();
		display.pack();
		
		setTheme("Default");
	}
	
	
	//just for display purposes as of now...
	public void initMenu() {
		JMenuBar menubar = new JMenuBar();
		JMenu game = new JMenu("Game");
		
		JMenu newGame = new JMenu("New Game");
		veryEasyMenuItem = new JMenuItem("Very Easy");
		easyMenuItem = new JMenuItem("Easy");
		medMenuItem = new JMenuItem("Medium");
		hardMenuItem = new JMenuItem("Hard");
		
		howToPlayMenuItem = new JMenuItem("How to Play");
		//will enable user to ignore game rules to test game functions - like win state/etc
		devModeMenuItem = new JCheckBoxMenuItem("Developer Mode"); 
		
		game.add(newGame);
		newGame.add(veryEasyMenuItem);
		newGame.add(easyMenuItem);
		newGame.add(medMenuItem);
		newGame.add(hardMenuItem);
		
		game.add(howToPlayMenuItem);
		game.add(devModeMenuItem);
		menubar.add(game);
		display.setJMenuBar(menubar);
	}
	
	public void displayHelpScreen() {
		if(gamePanel != null) {
			display.getContentPane().remove(gamePanel);
			display.revalidate();
		} if(winScreen != null) {
			display.getContentPane().remove(winScreen);
			display.revalidate();
			winScreen = null;
		}
		helpScreen = new RulesPanel(this);
		helpScreen.setResources(resources);
		display.add(helpScreen, BorderLayout.CENTER);
		display.validate();
		statusPanel.getEndTurnButton().setEnabled(false);
		statusPanel.getEndSubTurnButton().setEnabled(false);
	}
	
	public void displayWinScreen(int turn) {
		if(gamePanel != null) {
			display.getContentPane().remove(gamePanel);
			display.revalidate();
			if(turn == 0) {
				winScreen = new GameOverPanel(true);
			} else {
				winScreen = new GameOverPanel(false);	
			}
			display.add(winScreen, BorderLayout.CENTER);
			display.validate();
			
			statusPanel.getEndTurnButton().setEnabled(false);
			statusPanel.getEndSubTurnButton().setEnabled(false);
		}
	}
	
	public void reset() {
		if(winScreen != null) {
			display.getContentPane().remove(winScreen);
			display.revalidate();
			winScreen = null;
		}
		if(helpScreen != null) {
			display.getContentPane().remove(helpScreen);
			display.revalidate();
			helpScreen = null;
		}
		display.getContentPane().add(gamePanel);
		display.validate();
		statusPanel.getEndTurnButton().setEnabled(true);
		statusPanel.getEndSubTurnButton().setEnabled(true);
		display.repaint();
	}
	
	public StatusPanel getStatusPanel() {
		return statusPanel;
	}
	
	public CapturePanel getCapturePanel1() {
		return capturePanel1;
	}
	
	public CapturePanel getCapturePanel2() {
		return capturePanel2;
	}
	
	public GamePanel getGamePanel() {
		return gamePanel;
	}
	
	public AttackPanel getAttackPanel() {
		return attackPanel;
	}
	
	public JMenuItem getVeryEasyMenuItem() {
		return veryEasyMenuItem;
	}
	
	public JMenuItem getEasyMenuItem() {
		return easyMenuItem;
	}
	
	public JMenuItem getMedMenuItem() {
		return medMenuItem;
	}
	
	public JMenuItem getHardMenuItem() {
		return hardMenuItem;
	}
	
	public JMenuItem getHowToPlayMenuItem() {
		return howToPlayMenuItem;
	}

	public JMenuItem getDevModeMenuItem() {
		return devModeMenuItem;
	}
	
	//set theme/look and feel of the game
	public void setTheme(String type) {
		switch(type) {
		case "Default":
			resources = GameResources.getDefault();
		}//can add more
		
		statusPanel.setTheme(resources);
		capturePanel1.setTheme(resources);
		capturePanel2.setTheme(resources);
		gamePanel.setTheme(resources);
		attackPanel.setTheme(resources);
	}
}
