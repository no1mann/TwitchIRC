package twitch.utils;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.Element;

import twitch.gui.TwitchChatDocument;
import twitch.gui.MutableAttributeSet.ClickableListener;

/*
 * Mouse event handler
 */
public class MouseHandler {
	
	//Executes clicks made in JTextPanes
	public static void executeClick(JTextPane pane, MouseEvent e){
		if(pane == null || pane.getDocument() == null)
			return;
		
		Element element = ((TwitchChatDocument)pane.getDocument()).getCharacterElement(pane.viewToModel(e.getPoint()));
		ClickableListener listen = (ClickableListener) element.getAttributes().getAttribute("link");
		if(listen != null)
			listen.execute();
	}
	
	//Updates the mouse cursor when hovering over clickable objects
	public static void updateMouse(JFrame frame, JTextPane pane, MouseEvent e){
		if(frame == null || pane == null || pane.getDocument() == null || !(pane.getDocument() instanceof TwitchChatDocument))
			return;
		
		Element element = ((TwitchChatDocument)pane.getDocument()).getCharacterElement(pane.viewToModel(e.getPoint()));
		ClickableListener listen = (ClickableListener) element.getAttributes().getAttribute("link");
		if(listen != null)
			frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
		else
			frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
}
