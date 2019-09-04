package cass.oli.revilo;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

public class Mouse extends MouseInputAdapter {
	Revilo revilo;

	public void mousePressed(MouseEvent e) {
		revilo.games[revilo.game].mouseX = e.getX();
        revilo.games[revilo.game].mouseY = e.getY();
    }
	public void mouseDragged(MouseEvent e) {
		revilo.games[revilo.game].dragging = true;
		revilo.games[revilo.game].dragX = e.getX();
		revilo.games[revilo.game].dragY = e.getY();
	}
	public void mouseMoved(MouseEvent e) {
		revilo.games[revilo.game].mouseX = e.getX();
		revilo.games[revilo.game].mouseY = e.getY();
	}
	 public void mouseReleased(MouseEvent e) {
		 if(e.getButton() == MouseEvent.BUTTON1) {
			 revilo.games[revilo.game].dragTo(e.getX(), e.getY());
			 revilo.games[revilo.game].dragging = false;
		 }
     }
	
	public Mouse(Revilo revilo) {
		this.revilo = revilo;
	}

}