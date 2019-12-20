package com.revilo.client;

//GameObject.java
//Oliver Cass
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

public class GameObject{
	public int x, y;
	public static int width = 32, height = width;
	
	public String type;
	public boolean walkable;
	public BufferedImage img;
	
	public GameObject(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public void render(Graphics g, Player player){
		g.drawImage(img, (Client.WIDTH-width)/2 + (x - player.X()), (Client.HEIGHT-height)/2 + (y - player.Y()), width, height, null);
	}
}

class Block{
	public String type;
	public int index;
	public BufferedImage img;
	public boolean walkable;
	
	public static int width = 32, height = width;
	
	public Block(String type, boolean walkable){
		this.type = type;
		this.walkable = walkable;
		img = null;
		try {
			img = ImageIO.read(new File("bin/img/vanilla." + type + ".png"));
		} catch (IOException e) {
			
		}
	}
}