package com.revilo.client;
//Client.java
//Oliver Cass

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Client extends Canvas implements Runnable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final int WIDTH = 500, HEIGHT = 500;
	static final int GRID = 32;
	/*
	 * Width, height and grid size must be even
	 * (width-grid)/2 should be an int
	 */
	
	public String name;
	public InetAddress address;
	public int port;
	Handler handler;
	Thread thread;
	boolean running = false;
	public int i = -1; //Temporary for debugging
	
	public Client(String host, int port){
		this.port = port;
		DatagramSocket socket = null;
		try {
			this.address = InetAddress.getByName(host);
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			socket = new DatagramSocket();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		//Scanner sc = new Scanner(System.in);
		boolean connected = false;
		
		while(!connected){
			i++;
			//System.out.println("Please Enter Your Name:");
			//name = sc.nextLine();
			try {
				byte[] name = ("oli" + i).getBytes("UTF-8");
				DatagramPacket packet = new DatagramPacket(name, name.length, address, port);
				socket.send(packet);
			
				byte[] buffer = new byte[1];
				packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				if(buffer[0] == 0) connected = true;
				else System.out.println("Name Taken.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//connected...
		handler = new Handler();
		handler.me = new Player(0, 0, 0, name, socket, port, address);
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("..\\Modpacks\\vanilla.txt"));
			String line = reader.readLine();
			while (line != null) {
				String[] args = line.split(" ");
				String type = args[0];
				boolean walkable = false;
				if(args[2].equals("true")) walkable = true;
				handler.blocks.add(new Block(type, walkable));
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
					
		this.setSize(WIDTH, HEIGHT);
		
		new Window("Revilo", this);
		
		Input input = new Input(socket, handler);
		Thread thread = new Thread(input);
		thread.start();
	}
	
	public synchronized void start(){
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	
	public synchronized void stop(){
		try{
			thread.join();
			running = false;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void run(){
		long lastTime = System.nanoTime();
		double amountOfTicks = 120.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		
		final int TARGET_FPS = 60;
		final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
		
		//int frames = 0;
		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){ //To stop getting behind
				tick();
				delta--;
			}
			
			render();
			//frames++;
			
			if(System.currentTimeMillis() - timer > 1000){ //per second
				timer += 1000;
				//System.out.println("FPS: " + frames);
				//frames = 0;
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
		stop();
	}
	
	private void tick(){
		handler.tick();
	}
	
	private void render(){
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null){
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		handler.render(g);
		
		g.dispose();
		bs.show();
	}
	
	public static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
		if(angle == 0) return img;

		double rads = Math.toRadians(angle);
		int w = img.getWidth();
		int h = img.getHeight();

		BufferedImage rotated = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = rotated.createGraphics();
		AffineTransform at = new AffineTransform();

		int x = w / 2;
		int y = h / 2;

		at.rotate(rads, x, y);
		g2d.setTransform(at);
		g2d.drawImage(img, 0, 0, null);
		g2d.dispose();

		return rotated;
	}
	
	public static int[] grid(int x, int y) {
		int block[] = new int[2];
		
		double gridX = x / GRID;
		double gridY = y / GRID;
		
		if(x < 0) gridX -= 1;
		block[0] = (int) Math.floor(gridX);
		
		if(y < 0) gridY -= 1;
		block[1] = (int) Math.floor(gridY);
		
		return block;
	}
	
	public static void main(String[] args){
		new Client("localhost", 3000);
	}
}