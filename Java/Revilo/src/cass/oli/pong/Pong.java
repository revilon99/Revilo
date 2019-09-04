package cass.oli.pong;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Pong extends Canvas implements Runnable{
	private static final long serialVersionUID = 1L;
	
	public int HEIGHT = 720, WIDTH = 1280;
	public static final float EPSILON_TIME = 1e-2f; //Threshold for zero time
	private static final int UPDATE_RATE = 60; //60 FPS
	private boolean running = true;
	public BoxContainer box;
	public ArrayList<GameObject> objects = new ArrayList<GameObject>();
	public ArrayList<GameObject> addObjects = new ArrayList<GameObject>();
	public ArrayList<GameObject> removeObjects = new ArrayList<GameObject>();
	private int objID = 0;
	
	public Pong() {
		this.setSize(WIDTH, HEIGHT);
		box = new BoxContainer(0, 0, WIDTH, HEIGHT);
	}
	
	public void tap(int x, int y) {
		//Organise top down in terms of layer height
		//return after each function
		
		//Pause Button
		if(x > WIDTH - 60 && x < WIDTH - 10 && y > 10 && y < 70) {
			running = !running;
			return;
		}
		//Remove Ball
		for(GameObject object : objects){
			if(object.shape == Shape.Circle) {
				Ball ball = (Ball) object;
				double xDist = x - ball.x;
	            double yDist = y - ball.y;
	            double dist = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
	            if(dist < ball.radius) {
	    			//ball.print();
	                removeBall(ball);
	                return;
	            }
			}
		}
		
		//Add Ball
		addBall(new Ball(x, y, objID));
		objID++;		
	}
	
	public void tick() {
		float timeLeft = 1.0f;
		
		//Synchronises adding and removing objects
		for(GameObject object : addObjects) {
			boolean add = true;
			
			switch(object.shape) {
			case Circle:
				Ball nBall = (Ball) object;
				if(        nBall.x - nBall.radius < 0
						|| nBall.x + nBall.radius > WIDTH
						|| nBall.y - nBall.radius < 0
						|| nBall.y + nBall.radius > HEIGHT) {
					add = false;
					break;
				}
				for(GameObject exist : objects) {
					switch(exist.shape) {
					case Circle:
						Ball ball = (Ball) exist;
						if(ball.ballCollision(nBall)) add = false;
						break;
					default:
						break;
					
					}
				}
				break;
			default:
				break;
			
			}
			
			if(add) objects.add(object);			
		}
		addObjects.clear();
		
		for(GameObject object : removeObjects) {
			objects.remove(object);
		}
		removeObjects.clear();
		
		do {
			float tMin = timeLeft;
			
			//Object-Object Collision
			for(int i = 0; i < objects.size(); i++) {
				for(int j = 0; j < objects.size(); j++) {
					if(i < j) {
						objects.get(i).intersect(objects.get(j), tMin);
						if(objects.get(i).earliestCollisionResponse.t < tMin) 
							tMin = objects.get(i).earliestCollisionResponse.t;
					}
				}
			}
			
			//Object-Wall Collision
			for(GameObject object : objects) {
				object.intersect(box, timeLeft);
				if(object.earliestCollisionResponse.t < tMin) {
					tMin = object.earliestCollisionResponse.t;
				}
			}
			//Move
			for(GameObject object : objects) object.update(tMin);
			
			if(tMin > 0.05) { //Do Not Display Small Changes
				render();
				try {
					Thread.sleep((long)(1000L / UPDATE_RATE * tMin));
				}catch(InterruptedException e) {}
			}
			timeLeft -= tMin;
		}while(timeLeft > EPSILON_TIME);
	}
	
	public void render() {
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
		g.fillRect(0, 0, WIDTH, HEIGHT);
				
		//Game Objects
		for(GameObject object : objects) {
			object.render(g);
		}
		
		//Pause Button
		g.setColor(Color.white);
		if(running) {
			g.fillRect(WIDTH - 60, 10, 20, 60);
			g.fillRect(WIDTH - 30, 10, 20, 60);
		}else {
			int[] xTri = {WIDTH- 60, WIDTH - 10, WIDTH - 60};
			int[] yTri = {10, 40, 70};
			g.fillPolygon(xTri, yTri, 3);
		}
		
		g.dispose();
		bs.show();
	}
	
	public void run(){
		while(true) {		
			long beginTimeMillis, timeTakenMillis, timeLeftMillis;
			beginTimeMillis = System.currentTimeMillis();
			
			if(running)  tick();
			render();
			
			timeTakenMillis = System.currentTimeMillis() - beginTimeMillis;
			timeLeftMillis = 1000L / UPDATE_RATE - timeTakenMillis;
			if(timeLeftMillis < 5) timeLeftMillis = 5;
			
			try {
				Thread.sleep(timeLeftMillis);
			}catch(InterruptedException e) {}
		}
	}
	
	public void resize() {
		this.WIDTH = getWidth();
		this.HEIGHT = getHeight();
		this.box = new BoxContainer(0, 0, WIDTH, HEIGHT);
	}
	
	public void addBall(Ball ball) {
		addObjects.add(ball);
	}
	public void removeBall(Ball ball) {
		removeObjects.add(ball);
	}
}
