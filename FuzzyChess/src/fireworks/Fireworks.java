package fireworks;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.JPanel;

public class Fireworks {
	private LinkedList<Spark> sparks;
	private Random generator;
	private JPanel canvas;
	
	public Fireworks(JPanel canvas) {
		this.canvas = canvas;
		sparks = new LinkedList<Spark>();
		generator = new Random();
	}
	
	public JPanel getCanvas() {
		return canvas;
	}
	
    public int sparksLeft() {
        return sparks.size();
    }

    public boolean removeSpark(Spark s) {
        return this.sparks.remove(s);
    }
    
    public Spark[] getSparks() {
    	return sparks.toArray(new Spark[0]);
    }

    public void explode(int x, int y) {
        int sparkCount = 50 + generator.nextInt(20);
        int grey = generator.nextInt(100) + 155;
        Color c = new Color(grey,grey,grey);
        long lifespan = 500 + generator.nextInt(500);

        int choice = generator.nextInt(100);

        if (choice < 25) {
            createCircleSpark(x, y, sparkCount, c, lifespan);
        } else if (choice < 50) {
            createMovingSpark(x, y, sparkCount, c, lifespan);
        } else if (choice < 75) {
            createBubbleSpark(x, y, sparkCount, c, lifespan);
        } else {
            createTrigSpark(x, y, sparkCount, c, lifespan);
        }
    }

    private void createCircleSpark(int x, int y, int sparkCount, Color c, long lifespan) {
        for (int i = 0; i < sparkCount; i++) {
            double direction = 360 * generator.nextDouble();
            double speed = 10 * generator.nextDouble() + 5;
            sparks.addLast(new CircleSpark(this, direction, x, y, c, lifespan, speed));
        }
    }

    private void createPerfectCircleSpark(int x, int y, int sparkCount, Color c, long lifespan) {
        sparkCount *= 2;

        lifespan /= 2;

        double speed = 20 * generator.nextDouble() + 5;

        for (int i = 0; i < sparkCount; i++) {
            double direction = 360 * generator.nextDouble();
            sparks.addLast(new PerfectCircleSpark(this, direction, x, y, c, lifespan, speed));
        }
    }

    private void createTrigSpark(int x, int y, int sparkCount, Color c, long lifespan) {
        for (int i = 0; i < sparkCount; i++) {
            double direction = 360 * generator.nextDouble();
            double speed = 10 * generator.nextDouble() + 5;
            sparks.addLast(new TrigSpark(this, direction, x, y, c, lifespan, speed));
        }
    }

    private void createMovingSpark(int x, int y, int sparkCount, Color c, long lifespan) {
        for (int i = 0; i < sparkCount; i++) {
            double direction = 360 * generator.nextDouble();
            double speed = 10 * generator.nextDouble() + 5;
            sparks.addLast(new MovingSpark(this, direction, x, y, c, lifespan, speed));
        }
    }

    private void createBubbleSpark(int x, int y, int sparkCount, Color c, long lifespan) {
        for (int i = 0; i < sparkCount; i++) {
            double direction = 360 * generator.nextDouble();
            double speed = 10 * generator.nextDouble() + 5;
            sparks.addLast(new BubbleSpark(this, direction, x, y, c, lifespan, speed));
        }
    }
}
