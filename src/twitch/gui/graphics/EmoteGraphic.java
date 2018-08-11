package twitch.gui.graphics;

import java.util.Random;

import javax.swing.Icon;

import twitch.message.emotes.Emote;

/*
 * Emote instance for emote overlay
 */
public class EmoteGraphic {
	
	private static final Random RANDOM = new Random();
	private static final double SPEED = 30.0 / EmoteWindow.FPS;
	
	private Emote emote;
	private double x, y = 0, yStart = 0;;
	private double time = 0;
	@SuppressWarnings("unused")
	private int windowWidth, windowHeight;
		
	public EmoteGraphic(Emote emote, int windowWidth, int windowHeight){
		this.emote = emote;
		this.windowHeight = windowHeight;
		this.windowWidth = windowWidth;
		x = RANDOM.nextInt(1000);
		yStart = RANDOM.nextInt(30);
	}
	
	//Move the emote down - simulates the effect of gravity
	public void move(){
		time++;
		y = SPEED * (((.25 * (100 - (time * time)))/-3.5) + 100) + yStart;
	}
	
	public boolean isVisable(){
		return y > -20 && y < windowHeight * 1.1;
	}
	
	public Emote getEmote(){
		return emote;
	}
	
	public Icon getImage(){
		return emote.getIcon();
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
}
