package com.revilo.client;

//Friend.java
//Oliver Cass
import java.awt.Graphics;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Friend{
	public static int width = 32, height = width;
	
	public int x, y;
	public int rotation;
	public BufferedImage img;
	public String name;
	public int ID;
	
	public Friend(int x, int y, int rotation, String name, int ID){
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		this.name = name;
		this.ID = ID;
		img = null;
		try {
			img = ImageIO.read(new File("bin/img/friend.png"));
		} catch (IOException e) {
			
		}
	}
	
	public void render(Graphics g, Player player){
		BufferedImage curr = Client.rotateImageByDegrees(img, rotation);
		g.drawImage(curr, (Client.WIDTH-width)/2 + (x - player.X()), (Client.HEIGHT-height)/2 + (y - player.Y()), width, height, null);
	}
}