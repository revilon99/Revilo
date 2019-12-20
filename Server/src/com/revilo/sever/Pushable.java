package com.revilo.sever;
import java.nio.ByteBuffer;
import java.util.ArrayList;
//Pushable.java
//Oliver Cass
//A game object that can be pushed by any player
import java.util.LinkedList;

public class Pushable extends GameObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Block block;
	
	public Pushable(int x, int y, Block block){
		super(x, y);
		this.block = block;
	}
	
	public boolean tick(ArrayList<Player> players, LinkedList<GameObject> objects){
		for(Player player : players){
			if(collision(player)){
				int dx = player.x - clientX();
				int dy = player.y - clientY();
				int newx = this.x;
				int newy = this.y;
				if(Math.abs(dx) > Math.abs(dy)){
					if(dx == 0) continue;
					newx -= dx/Math.abs(dx);
				}else{
					if(dy == 0) continue;
					newy -= dy/Math.abs(dy);
				}
				
				boolean skip = false;
				for(GameObject object : objects){
					if(object.x == newx && object.y == newy){
						skip = true;
						break;
					}
				}
				for(Player p : players){
					if(Math.round(p.x/width) == newx && Math.round(p.y/height) == newy){
						skip = true;
						break;
					}
				}
				if(skip) continue;
				x = newx;
				y = newy;
				return true;
			}
		}
		return false;
	}
	
	public byte[] update(){
		byte[] x = ByteBuffer.allocate(4).putInt(this.x).array();
		byte[] y = ByteBuffer.allocate(4).putInt(this.y).array();
		//{x1...x4, y1...y4, id,...}
		byte[] output = {x[0], x[1], x[2], x[3], y[0], y[1], y[2], y[3], (byte) block.index};
		return output;
	}
	
	public String write(){
		return block.type + " " + x + " " + y + "\n";
	}
}