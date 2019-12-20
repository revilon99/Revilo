package com.revilo.client;
//Biome.java
//Oliver Cass

import java.util.ArrayList;

public class Biome extends GameObject{
	public Biome(int x, int y, ArrayList<Block> blocks){
		super(x*width, y*height);
		
		int x_n = x / 15;
		int y_n = y / 20;
	
		double value = 0.5*(Math.sin(Math.pow(x_n/8, 3) + Math.pow(y_n/5, 2)) + Math.sin(Math.pow(x_n/5, 2) + Math.pow(y_n/3, 2))); 
		
		if(value > -0.2) type = "grass";
		else if(value > -0.7) type = "sand";
		else type = "water";
		
		for(Block block : blocks){
			if(block.type.equals(type)){
				img = block.img;
				walkable = block.walkable;
				break;
			}
		}
	}
}