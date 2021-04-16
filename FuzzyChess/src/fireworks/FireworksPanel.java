package fireworks;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

//class used for testing
public class FireworksPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1266778429484392409L;
    private Fireworks fireworks;
    private final Dimension MAX_DIMENSION = new Dimension(800, 800);
    private Random generator = new Random();
    
    private Timer explosionTimer;
    private Timer fireworksTimer;

    public FireworksPanel() {
        this.setPreferredSize(MAX_DIMENSION);
        this.setLayout(null);
        this.fireworks = new Fireworks(this);

        //for the actual explosions
        fireworksTimer = new Timer(15, this);
        explosionTimer = new Timer(1000, this);  
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
        g.setColor(Color.BLACK);
        Rectangle clip = g.getClip().getBounds();
        g.fillRect(0, 0, clip.width, clip.height);

        Graphics2D g2d = (Graphics2D)g;

        Spark sparks[] = fireworks.getSparks();

        for(Spark s : sparks) {
            s.draw(g2d);
        }
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == fireworksTimer) {
			if(fireworks.sparksLeft() > 0) {
                repaint();
			}
		}
		else if(e.getSource() == explosionTimer) {
			fireworks.explode(generator.nextInt(400)+200, generator.nextInt(400)+200);
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		FireworksPanel f = new FireworksPanel();
		frame.add(f);
		frame.pack();
		f.startFireworks();
	}
}