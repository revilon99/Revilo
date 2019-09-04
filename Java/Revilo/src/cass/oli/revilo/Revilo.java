package cass.oli.revilo;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import cass.oli.boids.Boids;
import cass.oli.mandelbrot.Mandelbrot;
import cass.oli.pong.Pong;

public class Revilo extends Canvas implements Runnable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JFrame frame;
	
	public static final int TARGET_FPS = 60;
	final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
	public static final int WIDTH = 1280, HEIGHT = 720;
	public int width = WIDTH, height = HEIGHT;
	private boolean running;
	
	public int game;
	public Game[] games;
	Mouse mouse;
	
	public Revilo() {
		this.setSize(WIDTH, HEIGHT);
		games = new Game[] {new Home(this), new Pong(this), new Mandelbrot(this), new Boids(this)};
		
		mouse = new Mouse(this);
		this.addMouseListener(mouse);
		this.addMouseMotionListener(mouse);
		
		frame = new JFrame("Revilo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setResizable(true);
		game = 0;
		
		frame.add(this);
		frame.pack();
		
		frame.addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	        	resize();
	        }
		});
		
		frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		run();
	}
	
	
	public void resize() {
		this.width = this.getWidth();
		this.height = this.getHeight();
		for(Game g : games) g.resize(width, height);
	}

	public static void main(String[] args) {
		new Revilo();
	}
	
	public void render() {
		//Render
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null){
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g1 = bs.getDrawGraphics();
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		games[game].render(g);
		g.dispose();
		bs.show();
	}
	
	public void run() {
		running = true;
		games[game].paused = false;
		while(running) {
			long lastTime = System.nanoTime();
			
			if(!games[game].paused) games[game].tick();
			render();
						
			long tick_time = (lastTime - System.nanoTime() + OPTIMAL_TIME)/1000000;
			if(tick_time > 0){
				try{
					Thread.sleep(tick_time);
				}catch(Exception e) {}
			}
		}
	}
}