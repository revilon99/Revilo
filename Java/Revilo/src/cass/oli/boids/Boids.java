package cass.oli.boids;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import cass.oli.maths.Vec;

public class Boids extends Canvas implements Runnable{
	/**
	 * https://rosettacode.org/wiki/Boids/Java
	 */
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 1280, HEIGHT = 720;
	public int width = WIDTH, height = HEIGHT;
	public ArrayList<Boid> boids = new ArrayList<>();
	
	private boolean running = false;
	private boolean paused = false;
	
	private final int num_boids = 75;
	
	public Vec target = null;
	private long target_timeout;
	
	public Boids() {
		this.setSize(WIDTH, HEIGHT);
		resize();
		for(int i = 0; i < num_boids; i++) boids.add(new Boid(this));
	}
	
	private void tick() {
		for(Boid boid : boids) boid.tick();
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null){
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g1 = bs.getDrawGraphics();
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
		        RenderingHints.VALUE_ANTIALIAS_ON);

		//Background
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		
		for(Boid boid : boids) boid.render(g);
		
		g.dispose();
		bs.show();
	}
	
	@Override
	public void run() {
		final int TARGET_FPS = 60;
		final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
		running = true;
		
		//int frames = 0;
		while(running){
			long lastTime = System.nanoTime();
			
			if(!paused) tick();
			render();
			
			if(target != null & (System.currentTimeMillis() - target_timeout) > 2000) target = null;			
			
			long tick_time = (lastTime - System.nanoTime() + OPTIMAL_TIME)/1000000;
			if(tick_time > 0){
				try{
					Thread.sleep(tick_time);
				}catch(Exception e) {}
			}
		}
	}
	
	public void resize() {
		width = this.getWidth();
		height = this.getHeight();
	}
	
	public void newTarget(int x, int y) {
		target = new Vec(x, y);
		target_timeout = System.currentTimeMillis();
	}
}
