package runner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Render extends JPanel {
	private final int WIDTH, HEIGHT;

	public Render(int width, int height) {
		setPreferredSize(new Dimension(width, height));
		this.WIDTH = width;
		this.HEIGHT = height;
		setBackground(Color.DARK_GRAY);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(new Color(50, 50, 50));
		g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);
		try {
			Thread.sleep(100);
			repaint();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
