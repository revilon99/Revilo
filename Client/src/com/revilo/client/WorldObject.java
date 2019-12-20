package com.revilo.client;

import java.util.ArrayList;

public class WorldObject extends GameObject{	
	public WorldObject(int x, int y, int type_index, ArrayList<Block> blocks){
		super(x, y);
		if(type_index < 0 || type_index >= blocks.size()) return;
		type =     blocks.get(type_index).type;
		img =      blocks.get(type_index).img;
		walkable = blocks.get(type_index).walkable;
	}	
}