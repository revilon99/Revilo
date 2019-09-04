package cass.oli.boids;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import cass.oli.maths.Vec;
import cass.oli.revilo.Game;
import cass.oli.revilo.Revilo;

public class Boids extends Game{
	/**
	 * https://rosettacode.org/wiki/Boids/Java
	 */
	public ArrayList<Boid> boids = new ArrayList<>();
	
	
	private final int num_boids = 75;
	
	public Vec target = null;
	private long target_timeout;
	private final int spriteRatio = 10;
	
	public Boids(Revilo revilo) {
		super(revilo);
		for(int i = 0; i < num_boids; i++) boids.add(new Boid(this));
	}
	
	public Boids() {
		for(int i = 0; i < num_boids; i++) boids.add(new Boid(this));
	}
	
	@Override
	protected void tick() {
		for(Boid boid : boids) boid.tick();
		if(target != null & (System.currentTimeMillis() - target_timeout) > 3000) target = null;
	}
	
	@Override
	protected void render(Graphics2D g){
		//Background
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		
		for(Boid boid : boids) boid.render(g);
		
		//Pause Button
		g.setColor(Color.white);
		if(!paused) {
			g.fillRect(width - 60, 10, 20, 60);
			g.fillRect(width - 30, 10, 20, 60);
		}else {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, width, height);
			g.setColor(Color.white);
			
			int nButtons = 1;
			int spriteWidth = (int) Math.round(width / spriteRatio); 
			int offset = (int) Math.round((height - spriteWidth * nButtons)/(nButtons+1));
			g.drawImage(sprites[0], (width - spriteWidth)/2, offset, spriteWidth, spriteWidth, null);
			
			int[] xTri = {width- 60, width - 10, width - 60};
			int[] yTri = {10, 40, 70};
			g.fillPolygon(xTri, yTri, 3);
		}
	}
	
	public void newTarget(int x, int y) {
		target = new Vec(x, y);
		target_timeout = System.currentTimeMillis();
	}

	@Override
	public void leftClick(int x, int y) {
		int nButtons = 1;
		int spriteWidth = (int) Math.round(width / spriteRatio); 
		int offset = (int) Math.round((height - spriteWidth * nButtons)/(nButtons+1));

		if(x > width - 60 && x < width - 10 && y > 10 && y < 70) {
			paused = !paused;
			return;
		}
		
		if(paused) {
			//Home
			if(		x > (width - spriteWidth)/2 && x < (width + spriteWidth)/2 &&
					y > offset && y < offset + spriteWidth) {
				revilo.game = 0;
			}
		}else {
			newTarget(x, y);	
		}
	}

	@Override
	public void rightClick(int x, int y) {
		target = null;
	}

	@Override
	public void dragTo(int x, int y) {
		leftClick(x, y);
	}
}
