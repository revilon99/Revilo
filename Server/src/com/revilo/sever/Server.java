package com.revilo.sever;
//Server.java
//Oliver Cass
//Socket demo for a multiplayer game
import java.io.*;
import java.util.*;

import java.net.*;
import java.nio.ByteBuffer;

public class Server implements Runnable{
	private final static int BUFFER = 1024;
	public final static int GRID = 32;
	
	public ArrayList<Player> players;
	public LinkedList<GameObject> objects;
	public ArrayList<Block> blocks;
	
	public boolean update; //stops issues from multiple threads
	
	DatagramSocket socket;
	
	public Server(int port){
		try{
			//Define initial variables
			players = new ArrayList<Player>();
			objects = new LinkedList<GameObject>();
			blocks  = new ArrayList<Block>();
			update = false;
			ByteBuffer buffer;
			
			//Load modpack (same as client)
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader("..\\Modpacks\\vanilla.txt"));
				String line = reader.readLine();
				int i = 0;
				while (line != null) {
					String[] args = line.split(" ");
					blocks.add(new Block(args[0], i, args[1]));
					
					line = reader.readLine();
					i++;
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//load map
			FileInputStream fileInputStream = null;
			try {
				File file = new File("bin/data");
				byte[] map = new byte[(int) file.length()];
				
				//read file into bytes[]
				fileInputStream = new FileInputStream(file);
				fileInputStream.read(map);
				
				for(int i = 1; i < map.length; i+=9) {
					buffer = ByteBuffer.wrap(map, i, 9);
					int x = buffer.getInt();
					int y = buffer.getInt();
					int id = buffer.get();
					addObjectByIndex(x, y, id);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			//create socket
			socket = new DatagramSocket(port);
			System.out.println("Server Started on *:" + port);
			Thread game_thread = new Thread(this);
			game_thread.start(); //run as output thread
			
			//begin as listener thread			
			byte[] buf = new byte[BUFFER];
			
			int id = 0; //Id of players, should be determined by login system
			//limits players to 256 (this occurs multiple times)
			
			while (true) {
				try {
					Arrays.fill(buf, (byte)0);
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					socket.receive(packet);
					
					InetAddress clientAddress = packet.getAddress();
					int clientPort = packet.getPort();
					
					boolean exists = false;
					for(Player player : players) {
						if(player.cl.toString().equals(clientAddress.toString())
						&& player.cp == clientPort) {
							player.in(buf);
							exists = true;
							break;
						}
					}
					
					if(!exists) {
						byte[] name_byte = new byte[packet.getLength()];
						for(int i = 0; i < name_byte.length; i++) name_byte[i] = buf[i];
							String name = new String(name_byte, "UTF-8"); //add name overall
							boolean taken = false;
							for(Player p : players) {
								if(name.equals(p.name)) {
									taken = true;
									break;
								}
							}
							if(taken) {
								new Player(name, (byte) id, clientAddress, clientPort, this, true);
							}else {
								System.out.println(name + " (" + id + ") has connected.");
								newPlayer(new Player(name, (byte) id, clientAddress, clientPort, this, false));
								id++;
							}
					}
				} catch(Exception e) {
					System.err.println(e);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void outAll(byte[] message){
		if(players.size() == 0) return;
		for(Player player : players){
			if(player.name == null) continue;
			player.out(message);
		}
	}
	
	public void outAllBut(byte[] message, String name){
		if(players.size() < 2) return;
		for(Player player : players){
			if(player.name == null) continue;
			if(player.name.equals(name)) continue;
			player.out(message);
		}
	}
	
	public void newPlayer(Player p){
		for(Player player : players){
			if(player.name == null) continue;
			byte[] name_byte = null;
			try {
				name_byte = p.name.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			byte[] output = new byte[p.name.length() + 2];
			output[0] = 1;
			output[output.length - 1] = p.ID;
			for(int i = 1; i < output.length - 1; i++) {
				output[i] = name_byte[i-1];
			}
			player.out(output);
		}
		players.add(p);
		update = true;
	}
	
	public void tick(){
		for(Player player : players){
			if(player.name == null) continue;
			if(System.nanoTime() - player.timeout > (long) 1000000000 * 5) {
				//Remove player if no message received in last 5 seconds
				System.out.println(player.name + " (" + player.ID + ") has disconnected");
				byte[] removePlayer = {3, player.ID};
				outAll(removePlayer);
				players.remove(player);
				return;
			}
			for(Player p : players){
				if(p.name == null) continue;
				if(p.name.equals(player.name)) continue;
				byte[] x = ByteBuffer.allocate(4).putInt(p.x).array();
				byte[] y = ByteBuffer.allocate(4).putInt(p.y).array();
				byte[] rot = ByteBuffer.allocate(4).putInt(p.rotation).array();
				byte[] output = {2, p.ID, x[0], x[1], x[2], x[3], y[0], y[1], y[2], y[3], rot[0], rot[1], rot[2], rot[3]};
				player.out(output);
			}
		}
		
		try {
			for(GameObject object : objects){
				if(object.tick(players, objects)) { //If there is a change in game objects update to all players
					update = true;
				}
			}
		}catch(Exception e){
			//Do nothing, linkedlist just changed size while iterating through it
		}
		//only updates on tick thread therefore does not change size while sending to player
		if(update) updateObjects();
	}
	
	public void updateObjects(){
		//create byte array of map
		byte[] map = Map();
		
		outAll(map);
		writeData(map);
		update = false;
	}
	
	public void addObject(int x, int y, String type) {
		for(GameObject object : objects) {
			if(object.x == x && object.y == y) return;
		}
		for(Block block : blocks) {
			if(block.type.equals(type)) {
				switch(block.property) {
					case("static"):
						objects.add(new Static(x, y, block));
						break;
					case("pushable"):
						objects.add(new Pushable(x, y, block));
						break;
				}
				break;
			}
		}
		
		update = true;
	}
	
	public void addObjectByIndex(int x, int y, int index) {
		for(GameObject object : objects) {
			if(object.x == x && object.y == y) return;
		}
		
		for(Block block : blocks) {
			if(block.index == index) {
				switch(block.property) {
					case("static"):
						objects.add(new Static(x, y, block));
						break;
					case("pushable"):
						objects.add(new Pushable(x, y, block));
						break;
				}
				update = true;
				break;
			}
		}
	}
	
	public void removeObject(int x, int y) {
		for(GameObject object : objects) {
			if(object.x == x && object.y == y) {
				objects.remove(object);
				update = true;
				break;
			}
		}
	}
	
	public void writeData(byte[] map){
		FileOutputStream fileOuputStream = null;
		try {
			fileOuputStream = new FileOutputStream("bin/data");
			fileOuputStream.write(map);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileOuputStream != null) {
				try {
					fileOuputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        }
	}
	
	public byte[] Map() {
		int noObjects = objects.size();
		byte[] map = new byte[noObjects*9 + 1];
		map[0] = 4;
		int i = 1;
		for(GameObject object : objects) {
			byte[] obj = object.update();
			for(int j = 0; j < 9; j++) {
				map[i+j] = obj[j];
			}
			i += 9;
		}
		return map;
	}
	
	public static void main(String[] args){
		new Server(3000);
	}

	public void run(){
		long lastTime = System.nanoTime();
		double amountOfTicks = 120.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		
		final int TARGET_FPS = 60;
		final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
		
		while(true){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				tick();
				delta--;
			}
			
			long tick_time = (lastTime - System.nanoTime() + OPTIMAL_TIME)/1000000;
			if(tick_time > 0){
				try{
					Thread.sleep(tick_time);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
}