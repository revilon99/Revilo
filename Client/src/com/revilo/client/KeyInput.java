package com.revilo.client;
//KeyInput.java
//Oliver Cass
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.*;

public class KeyInput extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private Handler handler;
	public static final int MOVES = 5;
		
	public KeyInput(Handler handler){
		setPreferredSize(new Dimension(Client.WIDTH, Client.HEIGHT));
		
		this.handler = handler;
		
		/*
		InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = getActionMap();
		*/
		
		int mapName = JComponent.WHEN_IN_FOCUSED_WINDOW;
		int[] keys = { //interpreted by player
			KeyEvent.VK_W, //up
			KeyEvent.VK_A, //down
			KeyEvent.VK_S, //left
			KeyEvent.VK_D, //right
		};
		int[] modifiers = { //key listeners would avoid this but they do not work properly
			0, 
			InputEvent.CTRL_DOWN_MASK,
			InputEvent.SHIFT_DOWN_MASK, 
			InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK,
			InputEvent.ALT_DOWN_MASK,			
			InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK,
			InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK,
			InputEvent.META_DOWN_MASK, 
			InputEvent.ALT_GRAPH_DOWN_MASK
		};
		for (int k : keys) {
			for(int m : modifiers){
				getInputMap(mapName).put(KeyStroke.getKeyStroke(k, m, false),
					"" + k + " Press");
				getInputMap(mapName).put(KeyStroke.getKeyStroke(k, m, true),
					"" + k + " Release");
			}
			
			getActionMap().put("" + k + " Press",
				new KeyBoardControl(handler.me, k, true));

			getActionMap().put("" + k + " Release",
				new KeyBoardControl(handler.me, k, false));
		}
	}
	
	class KeyBoardControl extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public int key;
		public Player player;
		public boolean keyPress;

		public KeyBoardControl(Player player, int key, boolean keyPress) {
			this.player = player;
			this.key = key;
			this.keyPress = keyPress;
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			if(player == null) System.out.println("null");
			if(keyPress){
				player.keyPressed(key);
			}
			else{
				player.keyReleased(key);
			}
		}
	}
}