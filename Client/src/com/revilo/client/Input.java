package com.revilo.client;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.LinkedList;

public class Input implements Runnable{
	Handler handler;
	String name;
	DatagramSocket socket;
	byte buf[];
	
	public Input(DatagramSocket s, Handler handler){
		this.socket = s;
		this.handler = handler;
	}
	
	public void run(){
		ByteBuffer buffer;
		/*
		 * Byte Keys:
		 * 0 - Start Game (connection established)
		 * 1 - Add Player      {name(the only UTF), ID}
		 * 2 - Update a player {ID, x..., y..., rot...}
		 * 3 - Remove a player {ID}
		 * 4 - Update All Objects {x1...x4, y1...y4, id,...}
		 * 5 - Add an Object {
		 */	
		buf = new byte[1024];
		while(true){
			try{
				//Arrays.fill(buf, (byte) 0);
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				try {
					socket.receive(packet);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				buffer = ByteBuffer.wrap(packet.getData());
				
				switch(buffer.get()){
					case(0): //Start Game
						System.out.println("Successfully Connected!");
						handler.updateBiome();
						break;
					case(1): //New Player
						int length = packet.getLength();
						if(length < 3) break;
						int name_length = length - 3;
						byte[] name = new byte[name_length];
						buffer.get(name);
						String new_name = new String(name, "UTF-8");
						int player_id = (int) buf[packet.getLength()-1];
						String message = ("New Player: " + new_name + " (" + player_id + ") has connected!\n");
						System.out.print(message);
						handler.addPlayer(new Friend(0, 0, 0, new_name, player_id));
						break;
					case(2): //Update Player
						int update_ID = buffer.get();
						/*
						 * limited to 256 players 
						 */
						
						for(Friend player : handler.players){
							if(player.ID == update_ID){
								player.x = buffer.getInt();
								player.y = buffer.getInt();
								player.rotation = buffer.getInt();
								break;
							}
						}
						break;
					case(3): //Remove Player
						int remove_ID = buffer.get();
						for(Friend player : handler.players){
							if(player.ID == remove_ID){
								System.out.println(player.name + " (" + player.ID + ") has disconnected.");
								handler.removePlayer(player);
								break;
							}
						}
						break;
					case(4): //Update all objects
						LinkedList<WorldObject> objects = new LinkedList<WorldObject>();
						for(int i = 0; i < packet.getLength()/9; i++) {
							int x = buffer.getInt()*Client.GRID;
							int y = buffer.getInt()*Client.GRID;
							byte[] id = new byte[1];
							buffer.get(id);
							int ID = (int) id[0];
							objects.add(new WorldObject(x, y, ID, handler.blocks));
						}
						handler.objects = objects;
						break;
					default:
						System.out.println("Unrecognised Command From Server");
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}