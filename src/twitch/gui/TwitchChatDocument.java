package twitch.gui;

import javax.swing.Icon;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;

/*
 * On-screen chat manager:
 * 
 * Manages appensions, updates, and deletions of chat messages
 */
public class TwitchChatDocument extends DefaultStyledDocument{

	private static final long serialVersionUID = 1L;
	private static final int MAX_LINES = 150;
	
	//Lock for updates to the document
	public final Object PRINT_LOCK = new Object();
	
	public TwitchChatDocument(){}
	
	//Removes lines if number of chat messages exceeds max lines
	public void removeLines() throws Exception {
		synchronized (PRINT_LOCK) {
			Element root = getDefaultRootElement();
			while (root.getElementCount() > MAX_LINES)
				removeFromStart(root);
		}
	}
	
	//Removes first element from the document
	private void removeFromStart(Element root) throws Exception {
		remove(0, root.getElement(0).getEndOffset());
	}
	
	//Adds text to the end of the document with custom attributes and new line
	public void appendText(String message, MutableAttributeSet attributes, boolean newLine){
		String print = message;
		if(newLine)
			print += "\n";
		appendText(print, attributes);
	}
	
	//Adds text to the end of the document with default attributes
	public void appendText(String message){
		appendText(message, AttributeList.getDefaultAttributeNoClone());
	}
	
	//Adds text to the end of the document with custom attributes
	public void appendText(String message, MutableAttributeSet attributes){
		processText(message, attributes, 0);
	}
	
	//Adds new line to the end of the document
	public void newLine(){
		this.appendText("\n");
	}
	
	//Adds space to the end of the document
	public void appendSpace(){
		appendText(" ");
	}
	
	//Adds image to the end of the document with default image attributes
	public void appendImage(Icon image, boolean newLine){
		MutableAttributeSet att = AttributeList.getImageAttribute();
		att.setImage(image);
		this.appendText(" ", att, newLine);
	}
	
	public void appendImage(Icon image){
		this.appendImage(image, false);
	}
	
	//Adds image to the end of the document with custom image attributes
	public void appendImage(MutableAttributeSet image, boolean newLine){
		this.appendText(" ", image, newLine);
	}
	
	public void appendImage(MutableAttributeSet image){
		this.appendText(" ", image);
	}
	
	//Adds attributes to the end of the document
	private void processText(String message, MutableAttributeSet attributes, int amount){
		//Lock required for concurrency of document updates
		synchronized (PRINT_LOCK) {
			//If the message doesn't exist, ignore
			if(message == null || message.length() == 0)
				return;
			try {
				//If the position of the insert is invalid, ignore
				if (getLength() < 0)
					throw new Exception("Invalid insert size");
				//Add the text too the screen
				insertString(getLength(), message, attributes);
			} catch (Exception e) {
				//The the appension fails more than 3 times, ignore
				if (amount >= 3)
					return;
				this.processText(message, attributes, amount + 1);
			}
		}
	}
	
}
