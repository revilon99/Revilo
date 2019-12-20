package com.revilo.sever;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

public class Static extends GameObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Block block;
	
	public Static(int x, int y, Block block){
		super(x, y);
		this.block = block;
	}
	
	public boolean tick(ArrayList<Player> players, LinkedList<GameObject> objects){
		//Do Nothing (Does not interact with players), just visual
		return false; //i.e. interaction causes no change
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