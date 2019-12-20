package com.revilo.client;
//Window.java
//Oliver Cass
//Canvas for client to play on
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import java.awt.*;
import java.awt.event.*;

public class Window extends Canvas{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Window(String title, Client client){
		JFrame frame = new JFrame(title);
		 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		KeyInput input = new KeyInput(client.handler);
		input.add(client);
		
		Mouse mouse = new Mouse(client);
		client.addMouseListener(mouse);
		client.addMouseMotionListener(mouse);
		
		frame.add(input);
		frame.pack();
		frame.setResizable(false);
		frame.setLocation(Client.WIDTH*client.i, 0); //debugging
		frame.setVisible(true);
		//frame.setAlwaysOnTop(true); 
		
		client.start();
	}
}

class Mouse extends MouseInputAdapter {
	Client client;
	public void mouseClicked(MouseEvent me) {
		if(me.getButton() == MouseEvent.BUTTON1) {
			client.handler.me.leftClick();
		}
		if(me.getButton() == MouseEvent.BUTTON3) {
			client.handler.me.rightClick(client.handler.blocks);
		}
	}
	public void mouseDragged(MouseEvent me) {
		if(me.getButton() == MouseEvent.BUTTON1) {
			client.handler.me.leftClick();
		}
		if(me.getButton() == MouseEvent.BUTTON3) {
			client.handler.me.rightClick(client.handler.blocks);
		}
	}
	
	public void mouseMoved(MouseEvent e) {
		client.handler.me.mousex = e.getX();
		client.handler.me.mousey = e.getY();
	}
	
	public Mouse(Client client) {
		this.client = client;
	}

}