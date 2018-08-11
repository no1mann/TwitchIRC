package twitch.gui;

import java.awt.event.KeyEvent;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import twitch.utils.KeyPressed;

public class ScrollManager {
	
	//When a scroll event is activated
	public static void scrollDown(JScrollPane scroll, JTextPane text){
		//If control key is held, ignore the scroll
		if(KeyPressed.isPressed(KeyEvent.VK_CONTROL))
			return;
		//If scroll window not initiated
		if(text == null || text.getDocument() == null || scroll == null)
			return;
		//If scroll bar can be updated
		if(text.getDocument().getLength() > scroll.getVerticalScrollBar().getMaximum() || text.getDocument().getLength()>0)
			//Updates the scroll
			try{
				text.setCaretPosition(text.getDocument().getLength());
			} catch(Exception e){
				System.out.println("Failed to scroll");
			}
	}
	
}
