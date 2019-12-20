package com.revilo.sever;
//Player.java
//Oliver Cass
//Player Class For Server
import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class Player{
	public int x, y;
	public int rotation;
	public int width = 32, height = width;
	private Server server;
	public String name;
	public byte ID;
	public ByteBuffer buffer;
	public InetAddress cl;
	public int cp;
	public long timeout;
	
	public Player(String name, byte ID, InetAddress cl, int cp, Server server, boolean name_taken){
		x = 0;
		y = 0;
		rotation = 0;
		this.server = server;
		this.ID = ID;
		this.name = name;
		this.cl = cl;
		this.cp = cp;
		timeout = System.nanoTime();
		if(name_taken) {
			byte[] name_warning = {1};
			out(name_warning);
		}else{
			byte[] start = {0};
			out(start); //name accepted
			out(start); //connection established
			for(Player player : server.players){
				byte[] name_byte = player.name.getBytes();
				byte[] output = new byte[name_byte.length + 2];
				output[0] = 1;
				output[output.length - 1] = player.ID;
				for(int i = 1; i < output.length - 1; i++) {
					output[i] = name_byte[i-1];
				}
				out(output);
			}
		}
	}
	
	public void in(byte[] in) {
		timeout = System.nanoTime();
		
		buffer = ByteBuffer.wrap(in);
		switch(buffer.get()){ //read() limits to 256 commands, this should be enough
			case(0): //Character Information
				this.x = buffer.getInt();
				this.y = buffer.getInt();
				this.rotation = buffer.getInt();
				break;
			case(1): //Add Objects
				int index = buffer.get();
				server.addObjectByIndex(buffer.getInt(), buffer.getInt(), index);
				break;
			case(2): //Remove Objects
				server.removeObject(buffer.getInt(), buffer.getInt());
				break;
		}
	}
	
	public void out(byte[] output) {
		DatagramPacket packet = new DatagramPacket(output, output.length, cl, cp);
	try {
			server.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int gridX() {
		double gridX = x / Server.GRID;
		if(x < 0) gridX -= 1;
		return (int) Math.floor(gridX);
	}
	
	public int gridY() {
		double gridY = y / Server.GRID;
		if(y < 0) gridY -= 1;
		return (int) Math.floor(gridY);
	}
}