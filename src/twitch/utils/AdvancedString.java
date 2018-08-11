package twitch.utils;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;

/*
 * Extra string methods
 */
public class AdvancedString {

	private final static BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	
	//Gets the pixel length of string
	public static int getFontLength(Font font, String text){
		FontMetrics fm = img.getGraphics().getFontMetrics(font);
		return fm.stringWidth(text);
	}
	
	//Shortens a string based on a defined font and pixel length
	public static String ellipsize(Font font, String text, int max) {
		//Gets pixel information of a font
		FontMetrics fm = img.getGraphics().getFontMetrics(font);
		int ellipsesSize = fm.stringWidth("...");
		String temp = text.toString();
		
		//If text is too small or if no shortening required, skip this
		if(max > ellipsesSize && fm.stringWidth(temp) > max){
			while(fm.stringWidth(temp) > max-ellipsesSize)
				temp = temp.substring(0, temp.length()-1);
			temp += "...";
		}
		return temp;
	}
	
}
