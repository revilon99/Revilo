package com.revilo.sever;
//GameObject.java
//Oliver Cass
//Basis for all game objects
import java.util.*;
import java.io.*;

public abstract class GameObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int width = 32, height = width;
	public int x, y; //location as discrete
	public ArrayList<Property> properties;
	public ArrayList<String[]> titles;
	
	public GameObject(int x, int y){
		this.x = x;
		this.y = y;
		
		properties = new ArrayList<Property>();
		titles = new ArrayList<String[]>();
	}
	
	public int clientX(){
		return width*x;
	}
	public int clientY(){
		return height*y;
	}
	
	public abstract boolean tick(ArrayList<Player> players, LinkedList<GameObject> objects);
	public abstract byte[] update();
	public abstract String write();
	
	public boolean collision(Player player){
		if(	player.x + player.width > clientX()   && 
			player.x < clientX() + this.width     &&
			player.y + player.height > clientY() && 
			player.y < clientY() + this.height) return true;
		
		return false;
	}
	
	public int getProperty(String name){
		for(Property property : properties){
			if(property.name.equals(name)) return property.value;
		}
		return -1;
	}
	
	public void setProperty(String name, int value){
		for(Property property : properties){
			if(property.name.equals(name)) {
				property.value = value;
				return;
			}
		}
		properties.add(new Property(name, value));
	}
	
	public void addProperty(String name, int value){
		for(Property property : properties){
			if(property.name.equals(name)) {
				property.value += value;
				return;
			}
		}
		properties.add(new Property(name, value));
	}
	
	public void removeProperty(String name){
		for(Property property : properties){
			if(property.name.equals(name)){
				properties.remove(property);
				return;
			}
		}
	}
	
	public String getTitle(String name){
		for(String[] property : titles){
			if(property[0].equals(name)) return property[1];
		}
		return "";
	}
	
	public void setTitle(String name, String value){
		for(String[] property : titles){
			if(property[0].equals(name)) {
				property[1] = value;
				return;
			}
		}
		String[] property = {name, value};
		titles.add(property);
	}
	
	public void removeTitle(String name){
		for(String[] property : titles){
			if(property[0].equals(name)){
				titles.remove(property);
				return;
			}
		}
	}
}

class Property{
	public String name;
	public int value;
	
	public Property(String name, int value){
		this.name = name;
		this.value = value;
	}
}