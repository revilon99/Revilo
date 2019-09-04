package cass.oli.revilo;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class Game{
	public Revilo revilo = null;
	public BufferedImage[] sprites;
	
	public int width, height;
	public int mouseX, mouseY;
	public int dragX, dragY;
	public boolean dragging = false;
	public boolean paused = false;
		
	public Game(Revilo revilo) {
		this.revilo = revilo;
		loadImages();
	}
	
	public Game() {
		loadImages();
	}
	
	private void loadImages() {
		sprites = new BufferedImage[3];
		try {
			sprites[0] = ImageIO.read(getClass().getResource("/res/Home.png"));
			sprites[1] = ImageIO.read(getClass().getResource("/res/Reload.png"));
			sprites[2] = ImageIO.read(getClass().getResource("/res/Art.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void resize(int w, int h) {
		this.width = w;
		this.height = h;
	}
	
	public abstract void leftClick(int x, int y);
	public abstract void rightClick(int x, int y);
	public abstract void dragTo(int x, int y);
	protected abstract void tick();
	protected abstract void render(Graphics2D g);
}
