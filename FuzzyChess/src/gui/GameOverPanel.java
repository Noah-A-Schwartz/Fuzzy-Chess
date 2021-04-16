package gui;

import fireworks.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

//can run as standalone if you want to test
public class GameOverPanel extends JPanel implements ActionListener {
	private ArrayList<Star> stars = new ArrayList<Star>();
	private ArrayList<GeneralPath> trees;
	private Fireworks fireworks;
    private Timer starTimer;
    private Timer explosionTimer;
    private Timer fireworksTimer;
    private Random generator;
    private boolean win;
    
	public GameOverPanel(boolean win) {
		this.win = win;
		this.setPreferredSize(new Dimension(800,800));
		fireworks = new Fireworks(this);
		generator = new Random();
		starTimer = new Timer(25, this);
		explosionTimer = new Timer(1000, this);
		fireworksTimer = new Timer(15, this);
		trees = createTrees(new Rectangle(0,0,800,800), 20);
		stars = createStars(new Rectangle(0,0,800,800), 20);
		if(win) {
			startFireworks();
		}
		startStars();
	}
	
	public void startStars() {
		starTimer.start();
	}
	
	public void startFireworks() {
		fireworksTimer.start();
        explosionTimer.start();
	}
	
	public void stopFireworks() {
		fireworksTimer.stop();
        explosionTimer.stop();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		Rectangle2D clip = g2d.getClip().getBounds();
		drawScene(g2d, clip);
	}
	
	private void drawFireworks(Graphics2D g) {
		Spark sparks[] = fireworks.getSparks();
		for(Spark s : sparks) {
			s.draw(g);
		}
	}
	
	public void drawScene(Graphics2D g2d, Rectangle2D clip) {
		g2d.setColor(Color.BLACK);
		g2d.fill(clip);
		drawMoon(g2d, 30,30,60,60);
		if(win) {
			drawFireworks(g2d);
		}
		g2d.setColor(new Color(100,100,100,255));
		g2d.fill(new Arc2D.Double(-200, clip.getHeight()*(3.0/4.0), clip.getWidth()*2, 400, 0, 180, Arc2D.OPEN));
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("TimesRoman", Font.PLAIN, 45));
		if(!win) {
			g2d.drawString("YOU LOSE", (int)clip.getWidth()/2-100, (int)clip.getHeight()/2);
		} else {
			g2d.drawString("YOU WIN!", (int)clip.getWidth()/2-100, (int)clip.getHeight()/2);
		}
		for(GeneralPath tree : trees) {
			g2d.setColor(new Color(75,75,75,255));
			g2d.fill(tree);
			g2d.draw(tree);
		}
		if(!win) {
			drawFlag(g2d, clip.getWidth()/2, clip.getHeight() * (2.85/4.0), 40,50, Color.WHITE);
		}
		for(Star s : stars) {
			s.draw(g2d);
		}
	}
	
	public void drawTree(Graphics2D g, double x, double y, double w, double h) {
		double xPoints[] = {(x+w/2.0), (x+w/2.0), x, (x+w/2.0), x+w, (x+w/2.0)};
		double yPoints[] = {y+h, y + (h-(h/6.0)), y + (h-(h/6.0)), y, y + (h-(h/6.0)), y + (h-(h/6.0))};
		GeneralPath treeShape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
		treeShape.moveTo(xPoints[0], yPoints[0]);
		for(int i = 1; i < xPoints.length; i++) {
			treeShape.lineTo(xPoints[i],yPoints[i]);
		}
		
		g.draw(treeShape);
		g.fill(treeShape);
	}
	
	public void drawFlag(Graphics2D g, double x, double y, double w, double h, Color c) {
		double xPoints[] = {x, x, (x+w), (x+w), x};
		double yPoints[] = {(y+h), y, y, y+(w-2*w/5), y+(w-2*w/5)};
		GeneralPath flagShape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
		flagShape.moveTo(xPoints[0], yPoints[0]);
		for(int i = 1; i < xPoints.length; i++) {
			flagShape.lineTo(xPoints[i],yPoints[i]);
		}
		g.setColor(c);
		g.fill(flagShape);
		g.draw(flagShape);
	}
	
	public void drawMoon(Graphics2D g, double x, double y, double w, double h) {
		g.setColor(new Color(35,35,35,255));
		g.fill(new Arc2D.Double(x-(w*7)/4, y-(h*7)/4, (w*7), (h*7), 0, 360, Arc2D.OPEN));
		g.setColor(new Color(100,100,100,255));
		g.fill(new Arc2D.Double(x-(w*4)/4, y-(w*4)/4, (w*4), (h*4), 0, 360, Arc2D.OPEN));
		g.setColor(Color.WHITE);
		g.fill(new Arc2D.Double(x, y, w*2, h*2, 0, 360, Arc2D.OPEN));
	}
	
	public void drawStar(Graphics2D g, double x, double y, double w, double h) {
		g.setColor(Color.WHITE);
		g.draw(new Line2D.Double(x, y+(h/2), x+w, y+(h/2)));
		g.draw(new Line2D.Double(x+(w/2), y, x+(w/2), y+h));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == fireworksTimer) {
			if(fireworks.sparksLeft() > 0) {
                repaint();
			}
		}
		else if(e.getSource() == explosionTimer) {
			fireworks.explode(generator.nextInt(400)+100, generator.nextInt(400)+100);
		}
		else if(e.getSource() == starTimer) {
			repaint();
		}
	}
	
	private class Star{
		public double xPos;
		public double yPos;
		public double width;
		public double height;
		public int alpha;
		public boolean isFading;
		
		public Star(double x, double y, double w, double h, int brightness) {
			xPos = x;
			yPos = y;
			width = w;
			height = h;
			alpha = brightness;
			isFading = (int)(Math.random() * 100) % 2 == 0 ? true : false;
		}
		
		public void draw(Graphics2D g) {
			step();
			
			g.setColor(new Color(255,255,255,alpha));
			g.draw(new Line2D.Double(xPos, yPos+(height/2), xPos+width, yPos+(height/2)));
			g.draw(new Line2D.Double(xPos+(width/2), yPos, xPos+(width/2), yPos+height));
		}
		
		public void step() {
			if(isFading) {
				alpha -= 5;
			}
			else {
				alpha += 5;
			}
			if(alpha <= 5 || alpha >= 250) {
				isFading = !isFading;
			}
		}
	}
	
	private ArrayList<Star> createStars(Rectangle clip, int count){
		ArrayList<Star> stars = new ArrayList<Star>();
		for(int i = 0; i < count; i++) {
			double x = Math.random() * clip.getWidth()-50 + 50;
			double y = (Math.random() * 450);
			double w = 25;
			double h = 25;
			int a = generator.nextInt(245) + 5;
			stars.add(new Star(x,y,w,h,a));
		}
		
		return stars;
	}
	
	private ArrayList<GeneralPath> createTrees(Rectangle clip, int count) {
		ArrayList<GeneralPath> trees = new ArrayList<GeneralPath>();
		for(int i = 0; i < count; i++) {
			double x = (Math.random() * clip.getWidth() - 50) + 50;
			double y = (Math.random() * 150) + clip.getHeight() * (3.0/4.0);
			double w = 35;
			double h = 60;
			
			double xPoints[] = {(x+w/2.0), (x+w/2.0), x, (x+w/2.0), x+w, (x+w/2.0)};
			double yPoints[] = {y+h, y + (h-(h/6.0)), y + (h-(h/6.0)), y, y + (h-(h/6.0)), y + (h-(h/6.0))};
			GeneralPath treeShape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
			treeShape.moveTo(xPoints[0], yPoints[0]);
			for(int j = 1; j < xPoints.length; j++) {
				treeShape.lineTo(xPoints[j],yPoints[j]);
			}
			trees.add(treeShape);
		}
		return trees;
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		boolean win = false;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new GameOverPanel(win));
		frame.pack();
		frame.setVisible(true);
	}
}	
