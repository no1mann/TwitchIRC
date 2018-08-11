package twitch.utils;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;

/*
 * List of default fonts
 */
public class Fonts {

	public static Font defaultFont, liveFont, notLiveFont;
	
	static{
		defaultFont = new JLabel().getFont();
		Map<TextAttribute, Integer> fontAttributes = new HashMap<TextAttribute, Integer>();
		fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		liveFont = new Font(defaultFont.getFontName(), Font.BOLD, defaultFont.getSize()).deriveFont(fontAttributes);
		notLiveFont = new Font(defaultFont.getFontName(), Font.PLAIN, defaultFont.getSize());
	}
	
}
