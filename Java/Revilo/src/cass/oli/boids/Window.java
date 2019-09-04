package cass.oli.boids;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;

public class Window {

	public static void main(String[] args) {
		new Window("Boids", new Boids());
	}
	
	public Window(String title, Boids boids) {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	            boids.resize();
	        }
	});
		
		Mouse mouse = new Mouse(boids);
		boids.addMouseListener(mouse);
		boids.addMouseMotionListener(mouse);
		
		frame.add(boids);
		frame.pack();
		frame.setResizable(true);
		frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		boids.run();
	}

}

class Mouse extends MouseInputAdapter {
	Boids boids;
	public void mouseClicked(MouseEvent me) {
		if(me.getButton() == MouseEvent.BUTTON1) {
			boids.newTarget(me.getX(), me.getY());
		}
		if(me.getButton() == MouseEvent.BUTTON3) {
			boids.target = null;
		}
	}
	public void mouseDragged(MouseEvent me) {
		
	}
	
	public void mouseMoved(MouseEvent e) {
		
	}
	
	public Mouse(Boids boids) {
		this.boids = boids;
	}

}
