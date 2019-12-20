package com.revilo.client;

import java.util.ArrayList;
//Handler.java
//Oliver Cass
//Handles all game data for client
import java.util.LinkedList;

import java.awt.Graphics;
import java.nio.ByteBuffer;

public class Handler{
	public LinkedList<WorldObject> objects = new LinkedList<WorldObject>();
	public LinkedList<Friend> players = new LinkedList<Friend>();
	public Player me;
	
	public ArrayList<Block> blocks = new ArrayList<Block>();
	
	public LinkedList<Biome> biome = new LinkedList<Biome>();
	public int lastx, lasty;
	public static final int biomeSize = 20;
	
	public void tick(){
		if(me == null) return;
		try{
			LinkedList<GameObject> allObjects = new LinkedList<GameObject>();
			try {
				allObjects.addAll(biome);
				allObjects.addAll(objects);
			}catch(Exception e) {
				//do nothing
				//another thread changed the size of the array while it was iterating
			}
			
			me.tick(allObjects);
			
			if(Math.abs(me.x - lastx) > biomeSize*Biome.width/2 || Math.abs(me.y - lasty) > biomeSize*Biome.height/2) updateBiome();
			//in same thread so should not cause a problem
			
			/*
			 * bytes:
			 * 0: Character Location	{0, x..., y..., rot...}
			 * 1: Add Block 			{1, x..., y..., type_index}
			 * 2: Remove Block 			{2, x..., y...}
			 */
			byte[] x = ByteBuffer.allocate(4).putInt(me.X()).array();
			byte[] y = ByteBuffer.allocate(4).putInt(me.Y()).array();
			byte[] rot = ByteBuffer.allocate(4).putInt((int) Math.round(me.rotation)).array();
			
			byte[] output = {0, x[0], x[1], x[2], x[3], y[0], y[1], y[2], y[3], rot[0], rot[1], rot[2], rot[3]};
			me.out(output);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void render(Graphics g){
		if(me == null) return;
		try{
			for(Biome object : biome){
				object.render(g, me);
			}
		}catch(Exception e){
		}
		try{
			for(WorldObject object : objects){
				object.render(g, me);
			}
			for(Friend player : players){
				player.render(g, me);
			}
		}catch(Exception e){
		}

		me.render(g);
	}
	
	public void updateBiome(){
		biome.clear();
		lastx = me.X();
		lasty = me.Y();
		int x = (int) Math.round(me.x / Biome.width);
		int y = (int) Math.round(me.y / Biome.height);

		for(int i = x - biomeSize; i < x + biomeSize; i++){
			for(int j = y - biomeSize; j < y + biomeSize; j++){
				biome.add(new Biome(i, j, blocks));
			}
		}
	}
	
	public void addObject(WorldObject object){
		this.objects.add(object);
	}
	
	public void removeObject(WorldObject object){
		this.objects.remove(object);
	}
	
	public void addPlayer(Friend player){
		this.players.add(player);
	}
	
	public void removePlayer(Friend player){
		this.players.remove(player);
	}
}