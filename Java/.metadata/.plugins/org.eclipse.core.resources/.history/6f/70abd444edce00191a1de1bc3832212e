package cass.oli.pong;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;

public class Window {
	public static void main(String args[]) {
		new Window("Pong", new Pong());
	}
	
	public Window(String title, Pong pong) {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	            pong.resize();
	        }
	});
		
		Mouse mouse = new Mouse(pong);
		pong.addMouseListener(mouse);
		pong.addMouseMotionListener(mouse);
		
		frame.add(pong);
		frame.pack();
		frame.setResizable(true);
		frame.setVisible(true);
		
		pong.run();
	}
}

class Mouse extends MouseInputAdapter {
	Pong pong;
	public void mouseClicked(MouseEvent me) {
		if(me.getButton() == MouseEvent.BUTTON1) {
			pong.tap(me.getX(), me.getY());
		}
	}
	public void mouseDragged(MouseEvent me) {
		
	}
	
	public void mouseMoved(MouseEvent e) {
		
	}
	
	public Mouse(Pong pong) {
		this.pong = pong;
	}

}