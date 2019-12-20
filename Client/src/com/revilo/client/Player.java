package com.revilo.client;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedList;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.awt.event.KeyEvent;

public class Player{
	public static int width = Client.GRID, height = width;
	public double speed = 1.5;
	public int radius = 2;
	
	public double x, y;
	public double rotation;
	public double velX, velY;
	public String name;
	public BufferedImage img;
	DatagramSocket socket = null;
	InetAddress address;
	public int port;
	public boolean[] keys = new boolean[5];
	
	public int mousex = 0, mousey = 0;
	
	public Player(double x, double y, double rotation, String name, DatagramSocket socket, int port, InetAddress address){
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		this.name = name;
		this.socket = socket;
		this.port = port;
		this.address = address;
		velX = 0;
		velY = 0;
		img = null;
		try {
			img = ImageIO.read(new File("bin/img/player.png"));
		} catch (IOException e) {
			
		}
	}
	
	public int X(){
		return (int) Math.round(x);
	}
	public int Y(){
		return (int) Math.round(y);
	}
	
	public void leftClick(){ //Remove Block
		int block[] = getLookingAt();
		byte[] blockx = ByteBuffer.allocate(4).putInt(block[0]).array();
		byte[] blocky = ByteBuffer.allocate(4).putInt(block[1]).array();
		byte[] output = {2, blockx[0], blockx[1], blockx[2], blockx[3], blocky[0], blocky[1], blocky[2], blocky[3]};
		out(output);
	}
	public void rightClick(ArrayList<Block> blocks) { //Add Block
		String type = "wooden_floor"; //debugging
		int block[] = getLookingAt();
		byte type_index = -1; //limits to 256 types of blocks, can be changed
		for(int i = 0; i < blocks.size(); i++) {
			if(blocks.get(i).type.equals(type)) {
				type_index = (byte) i;
				break;
			}
		}
		byte[] blockx = ByteBuffer.allocate(4).putInt(block[0]).array();
		byte[] blocky = ByteBuffer.allocate(4).putInt(block[1]).array(); 
		
		byte[] output = {1, type_index, blockx[0], blockx[1], blockx[2], blockx[3], blocky[0], blocky[1], blocky[2], blocky[3]};
		out(output);
	}
	
	public void out(byte[] output) {
		try {
			socket.send(new DatagramPacket(output, output.length, address, port));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void keyPressed(int key) {
		switch(key){
			case(KeyEvent.VK_W):
				keys[0] = true;
				break;
			case(KeyEvent.VK_A):
				keys[2] = true;
				break;
			case(KeyEvent.VK_S):
				keys[1] = true;
				break;
			case(KeyEvent.VK_D):
				keys[3] = true;
				break;
		}
	}
	public void keyReleased(int key){
		switch(key){
			case(KeyEvent.VK_W):
				keys[0] = false;
				break;
			case(KeyEvent.VK_A):
				keys[2] = false;
				break;
			case(KeyEvent.VK_S):
				keys[1] = false;
				break;
			case(KeyEvent.VK_D):
				keys[3] = false;
				break;
		}
	}
	
	public void tick(LinkedList<GameObject> objects){
		velX = 0;
		velY = 0;
		
		if(keys[0]) velY -= 1;
		if(keys[1]) velY += 1;
		if(keys[2]) velX -= 1;
		if(keys[3]) velX += 1;
		
		if(velX == 0 && velY == 0) return;
		
		if(velY == 0 && velX != 0) rotation = 90*Math.abs(velX - 2);
		else if(velX == 0 && velY != 0) rotation = 90*(1 + velY);
		else if(velY != 0) {
			double rot = Math.toDegrees(Math.atan(velX/velY));
			rotation = rot + 90*(velX);
		}
		
		double dx = speed * Math.sin(Math.toRadians(rotation));
		double dy = -speed * Math.cos(Math.toRadians(rotation));
		
		if(!collision(x + dx, y + dy, objects)){
			x += dx;
			y += dy;
		}else if(!collision(x + dx, y, objects)){ //Sliding
			x += dx;
		}else if(!collision(x, y + dy, objects)){
			y += dy;
		}
	}
	
	public void render(Graphics g){
		BufferedImage curr = Client.rotateImageByDegrees(img, rotation);
		g.drawImage(curr, (Client.WIDTH-width)/2, (Client.HEIGHT-height)/2, width, height, null);
		g.setColor(Color.black); 
		int block[] = getLookingAt();
		g.drawRect((Client.WIDTH-width)/2 + (block[0]*width - X()), (Client.HEIGHT-height)/2 + (block[1]*height - Y()), width, height);
	}
	
	boolean collision(double x, double y, LinkedList<GameObject> objects){ //Can collide with player for now
		try{
			for(GameObject object : objects){
				if(!object.walkable){
					if(x + width > object.x  && x < object.x + Block.width &&
					   y + height > object.y && y < object.y + Block.height) return true;
				}
			}
		}catch(Exception e){}
		return false;
	}
	
	public int[] getLookingAt() {
		int dx = mousex - (Client.WIDTH - width)/2;
		int dy =  mousey - (Client.HEIGHT - height)/2;
		/*
		 * TODO
		 * Creates a square radius
		 * Make a circle in future
		 */
		if(dx > Client.GRID*radius) dx = Client.GRID*radius;
		if(dx < -Client.GRID*radius) dx = -Client.GRID*radius;
		
		if(dy > Client.GRID*radius) dy = Client.GRID*radius;
		if(dy < -Client.GRID*radius) dy = -Client.GRID*radius;
		
		int actualx = dx + X();
		int actualy = dy + Y();
		
		return Client.grid(actualx, actualy);
	}
}