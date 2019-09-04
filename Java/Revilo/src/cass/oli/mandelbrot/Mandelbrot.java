package cass.oli.mandelbrot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

public class Mandelbrot extends JLabel implements Runnable{
	/**
	 * TODO
	 * Better movement system
	 * have general image of Mandelbrot for scrolling, much quicker
	 * better zooming system
	 * ability to improve quality (number of iterations)
	 * Threading so a Mandelbrot sequence can be played out
	 * ability to save images and image sequences of Mandelbrot's
	 * see http://java.rubikscube.info/
	 */
	private static final long serialVersionUID = 1L;
	private int width = 1280, height = 720;
	private final int default_max = 6;
	private final int absolute_max = 12;
	private int current_max = 6;
	private Color[][] colors;
	private long lastRender;
	private static final int[][][] colpal = {
		{ {0, 10, 20}, {50, 100, 240}, {20, 3, 26}, {230, 60, 20},
		{25, 10, 9}, {230, 170, 0}, {20, 40, 10}, {0, 100, 0},
		{5, 10, 10}, {210, 70, 30}, {90, 0, 50}, {180, 90, 120},
		{0, 20, 40}, {30, 70, 200} },
		{ {70, 0, 20}, {100, 0, 100}, {255, 0, 0}, {255, 200, 0} },
		{ {40, 70, 10}, {40, 170, 10}, {100, 255, 70}, {255, 255, 255} },
		{ {0, 0, 0}, {0, 0, 255}, {0, 255, 255}, {255, 255, 255}, {0, 128, 255} },
		{ {0, 0, 0}, {255, 255, 255}, {128, 128, 128} },
	};
	private int pal = 0;
	  private static final int[][] rows = {
			    {0, 16, 8}, {8, 16, 8}, {4, 16, 4}, {12, 16, 4},
			    {2, 16, 2}, {10, 16, 2}, {6, 16, 2}, {14, 16, 2},
			    {1, 16, 1}, {9, 16, 1}, {5, 16, 1}, {13, 16, 1},
			    {3, 16, 1}, {11, 16, 1}, {7, 16, 1}, {15, 16, 1},
			  };

	private boolean smooth = true;
	private boolean antialias = true;
	private double viewX = 0.0;
	private double viewY = 0.0;
	private double zoom = 1.0;
	public boolean dragging = false;
	public int initX;
	public int initY;
	public int mouseX;
	public int mouseY;
	private BufferedImage image;

	public static void main(String[] args) {
		Mandelbrot man = new Mandelbrot();
		JPanel panel = new JPanel();
		
		JFrame frame = new JFrame("Mandelbrot");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addComponentListener(new ComponentAdapter() {  
			public void componentResized(ComponentEvent evt) {
				man.resize(panel);
			}
		});
	
		Mouse mouse = new Mouse(man);
		man.addMouseListener(mouse);
		man.addMouseMotionListener(mouse);
		
		
		panel.add(man);		
		frame.add(panel);
		frame.pack();
		frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		man.run();
	}
	
	public Mandelbrot() {
		colors = new Color[colpal.length][];
		for (int p = 0; p < colpal.length; p++) {
			colors[p] = new Color[colpal[p].length * 12];
			for (int i = 0; i < colpal[p].length; i++) {
				int[] c1 = colpal[p][i];
				int[] c2 = colpal[p][(i + 1) % colpal[p].length];
				for (int j = 0; j < 12; j++)
					colors[p][i * 12 + j] = new Color(
							(c1[0] * (11 - j) + c2[0] * j) / 11,
							(c1[1] * (11 - j) + c2[1] * j) / 11,
							(c1[2] * (11 - j) + c2[2] * j) / 11);
			}
		}
	}
	
	// Computes a color for a given point
	private Color color(double x, double y) {
		int count = mandel(x, y);
		int palSize = colors[pal].length;
		Color color = colors[pal][count / 256 % palSize];
		if (smooth) {
			Color color2 = colors[pal][(count / 256 + palSize - 1) % palSize];
			int k1 = count % 256;
			int k2 = 255 - k1;
			int red = (k1 * color.getRed() + k2 * color2.getRed()) / 255;
			int green = (k1 * color.getGreen() + k2 * color2.getGreen()) / 255;
			int blue = (k1 * color.getBlue() + k2 * color2.getBlue()) / 255;
			color = new Color(red, green, blue);
		}
		return color;
	}

	public void render() {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		// fractal image drawing
		for (int row = 0; row < rows.length; row++) {
			for (int y = rows[row][0]; y < height; y += rows[row][1]) {
				for (int x = 0; x < width; x++) {
					double r = zoom / Math.min(width, height);
					double dx = 2.5 * (x * r + viewX) - 2;
					double dy = 1.25 - 2.5 * (y * r + viewY);
					Color color = color(dx, dy);
					if (antialias) {
						Color c1 = color(dx - 0.25 * r, dy - 0.25 * r);
						Color c2 = color(dx + 0.25 * r, dy - 0.25 * r);
						Color c3 = color(dx + 0.25 * r, dy + 0.25 * r);
						Color c4 = color(dx - 0.25 * r, dy + 0.25 * r);
						int red = (color.getRed() + c1.getRed() + c2.getRed() + c3.getRed() + c4.getRed()) / 5;
						int green = (color.getGreen() + c1.getGreen() + c2.getGreen() + c3.getGreen() + c4.getGreen()) / 5;
						int blue = (color.getBlue() + c1.getBlue() + c2.getBlue() + c3.getBlue() + c4.getBlue()) / 5;
						color = new Color(red, green, blue);
					}
					g.setColor(color);
					g.fillRect(x, y - rows[row][2] / 2, 1, rows[row][2]);
				}
			}
		}
		this.setIcon(new ImageIcon(image));
		g.dispose();
	}

	public void update() {
		current_max = default_max;
		lastRender = System.currentTimeMillis();
		render();
	}
	
	private int mandel(double pRe, double pIm) {
		double zRe = 0;
		double zIm = 0;
		double zRe2 = zRe * zRe;
		double zIm2 = zIm * zIm;
		double zM2 = 0.0;
		int count = 0;
		int max = (int) Math.pow(2, current_max);
		while (zRe2 + zIm2 < 4.0 && count < max) {
			zM2 = zRe2 + zIm2;
			zIm = 2.0 * zRe * zIm + pIm;
			zRe = zRe2 - zIm2 + pRe;
			zRe2 = zRe * zRe;
			zIm2 = zIm * zIm;
			count++;
		}
		if (count == 0 || count == max) return 0;
		// transition smoothing
		zM2 += 0.000000001;
		return 256 * count + (int)(255.0 * Math.log(4 / zM2) / Math.log((zRe2 + zIm2) / zM2));
	}

	public void resize(JPanel panel) {
		width = panel.getWidth();
		height = panel.getHeight();
		update();
	}

	public void zoomIn(double scale) {
		viewX += (double) zoom*(scale - 1)/2;
		viewY += (double) zoom*(scale - 1)/2;
		zoom /= scale;
		
		update();
	}
	public void zoomOut(double scale) {
		viewX -= (double) zoom*(scale - 1)/2;
		viewY -= (double) zoom*(scale - 1)/2;
		zoom *= scale;
		update();
	}

	public void drag(int x, int y) {
		int dx = Math.abs(x - initX);
		int dy = Math.abs(y - initY);
		if(dx == 0 || dy == 0) return;
		double scale;
		if(dx < dy) scale = width / dx;
		else scale = height / dy;
		
		viewX += 2*zoom*initX/width;
		viewY += zoom*initY/height;
		
		zoom /= scale;
		update();
	}
	public void newPallette() {
		pal = (pal + 1) % colpal.length;
		update();
	}
	public void reset() {
		viewX = 0;
		viewY = 0;
		zoom = 1.0;
		update();
	}

	@Override
	public void run() {
		final int TARGET_FPS = 60;
		final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
		lastRender = System.currentTimeMillis();
		while(true){
			long lastTime = System.nanoTime();
			
			long timeSinceLastRender = System.currentTimeMillis() - lastRender;
			if(timeSinceLastRender > 4000) {
				if(current_max < absolute_max) current_max++;
				lastRender = System.currentTimeMillis();
				render();
			}
			
			if(dragging) {
				Graphics g = this.getGraphics();
				g.setColor(Color.white);
				double dx = Math.abs(mouseX - initX);
				double dy = Math.abs(mouseY - initY);
				if(dx == 0 || dy == 0) continue;
				double scale;
				if(dx < dy) scale = width / dx;
				else scale = height / dy;
				g.drawRect(initX, initY, (int) Math.round(width/scale), (int) Math.round(height/scale));
				this.paint(g);
				g.dispose();
				this.paint(g);
			}
			
			long tick_time = (lastTime - System.nanoTime() + OPTIMAL_TIME)/1000000;
			if(tick_time > 0){
				try{
					Thread.sleep(tick_time);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
}

class Mouse extends MouseInputAdapter {
	Mandelbrot man;
	public void mouseClicked(MouseEvent me) {
		if(me.getButton() == MouseEvent.BUTTON2) {
			man.newPallette();
		}
		if(me.getButton() == MouseEvent.BUTTON3) {
			man.reset();
		}
	}
	public void mousePressed(MouseEvent e) {
        man.initX = e.getX();
        man.initY = e.getY();
    }
	public void mouseDragged(MouseEvent e) {
		man.dragging = true;
		man.mouseX = e.getX();
		man.mouseY = e.getY();
	}
	public void mouseMove(MouseEvent e) {
	}
	 public void mouseReleased(MouseEvent e) {
         man.drag(e.getX(), e.getY());
         man.dragging = false;
     }
	
	public Mouse(Mandelbrot man) {
		this.man = man;
	}

}
