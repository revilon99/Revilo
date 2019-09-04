package cass.oli.revilo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Home extends Game {
	final int spriteRatio = 10;
	int nIcons = 3;
	BufferedImage[] games = new BufferedImage[nIcons];
	
	public Home(Revilo revilo) {
		super(revilo);
		try {
			games[0] = ImageIO.read(getClass().getResource("/res/Pong.png"));
			games[1] = ImageIO.read(getClass().getResource("/res/Mandelbrot.png"));
			games[2] = ImageIO.read(getClass().getResource("/res/Boids.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void leftClick(int x, int y) {
		int iconWidth = Math.round(width / spriteRatio);
		int offset = Math.round((width - nIcons*iconWidth)/(nIcons + 1));
		int iconY = Math.round((height - iconWidth) / 2);
		if(y < iconY || y > iconY + iconWidth) return;
		if(x > offset && x < offset + iconWidth) revilo.game = 1;
		if(x > 2*offset + iconWidth && x < 2*(offset + iconWidth)) revilo.game = 2;
		if(x > 3*offset + 2* iconWidth && x < 3*(offset + iconWidth)) revilo.game = 3;
		
	}

	@Override
	public void rightClick(int x, int y) {
		//Do Nothing
	}

	@Override
	protected void tick() {
		//Do Nothing for now
	}

	@Override
	protected void render(Graphics2D g) {
		//Background
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		
		int border = 2;
		int iconWidth = Math.round(width / spriteRatio);
		int offset = Math.round((width - nIcons*iconWidth)/(nIcons + 1));
		int y = Math.round((height - iconWidth) / 2);
		
		g.setColor(Color.white);
		g.fillRect(offset-border, y-border, iconWidth + 2*border, iconWidth + border*2);
		g.drawImage(games[0], offset, y, iconWidth, iconWidth, null);
		g.fillRect(2*offset + iconWidth-border, y-border, iconWidth + 2*border, iconWidth + border*2);
		g.drawImage(games[1], 2*offset + iconWidth, y, iconWidth, iconWidth, null);
		g.fillRect(3*offset + 2*iconWidth-border, y-border, iconWidth + 2*border, iconWidth + border*2);
		g.drawImage(games[2], 3*offset + 2*iconWidth, y, iconWidth, iconWidth, null);
	}

	@Override
	public void dragTo(int x, int y) {
		leftClick(x, y);
	}

}
