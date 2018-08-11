package twitch.utils;

import java.awt.Color;
import java.util.Random;

/*
 * Generates random colors
 */
public class RandomColor {

	private static final Random random = new Random();
	
	//Gets a random color
	public static final Color getRandomColor(){
		return new Color(random.nextInt(220)+15, random.nextInt(220)+15, random.nextInt(220)+15);
	}
}
