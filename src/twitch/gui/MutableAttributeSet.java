package twitch.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import twitch.gui.windows.UserModerationWindow;
import twitch.gui.windows.Window;
import twitch.message.ModerationTools;
import twitch.message.emotes.Emote;
import twitch.message.emotes.EmoteType;
import twitch.user.User;
import twitch.user.UserList;
import twitch.user.badge.BadgeType;
import twitch.utils.Dialogs;

/*
 * Design attributes for chat pane
 */
public class MutableAttributeSet extends SimpleAttributeSet{

	private static final long serialVersionUID = 1L;
	private Icon icon = null;
	private Color color = null;
	
	public MutableAttributeSet(){
		super();
	}
	
	
	/*
	 * CONFIGURABLE ATTRIBTUES
	 */
	public void setBold(boolean val){
		super.addAttribute(StyleConstants.Bold, val);
	}
	
	public void setItalics(boolean val){
		super.addAttribute(StyleConstants.Italic, val);
	}
	
	public void setUnderlined(boolean val){
		super.addAttribute(StyleConstants.Underline, val);
	}
	
	public void setFont(Font font){
		super.addAttribute(StyleConstants.FontFamily, font);
	}
	
	public void setColor(Color color){
		super.addAttribute(StyleConstants.Foreground, color);
		this.color = color;
	}
	
	public void setImage(Icon image){
		StyleConstants.setIcon(this, image);
		icon = image;
	}
	
	public void setFontSize(int size){
		super.addAttribute(StyleConstants.FontSize, size);
	}
	
	public void setSize(int size){
		super.addAttribute(StyleConstants.Size, size);
	}
	
	public void setBackground(Color color){
		super.addAttribute(StyleConstants.Background, color);
	}
	
	public void setClickable(ClickableType type, Object value){
		super.addAttribute("link", new ClickableListener(type, value));
	}
	
	public Icon getIcon(){
		return icon;
	}
	
	public Color getColor(){
		return color;
	}
	
	/*
	 * Defines the click action for this attribute
	 */
	public class ClickableListener extends AbstractAction{

		private static final long serialVersionUID = 1L;
		private Object value;
	    private ClickableType type;

	    public ClickableListener(ClickableType type, Object value) {
	        this.value = value;
	        this.type = type;
	    }

	    public void execute(){
	    	//Username was clicked: Opens moderation window
	    	if(type == ClickableType.USERNAME)
	    		UserModerationWindow.createUserWindow(UserList.getUser(Window.getCurrentConnection(), ((String)value).toLowerCase()));
	    	//Emote was clicked: Opens window displaying information about the emote
	    	else if(type == ClickableType.EMOTE){
	    		Emote emote = (Emote)value;
	    		String print = "Name: " + emote.getName() + 
	    				"\nChannel: " + emote.getChannel() + 
	    				"\nID: " + emote.getID() + 
	    				"\nFrom: " + emote.getEmoteType().name();
	    		//If emote is Twitch emote
	    		if(emote.getEmoteType()!=EmoteType.TWITCH)
	    			print += "\nCreator: " + emote.getCreator(); 
	    		Dialogs.iconMessage("Emote - " + emote.getName(), print, emote.getIcon());
	    	}
	    	//Badge icon was clicked: Opens window displaying information about the badge
	    	else if(type == ClickableType.BADGE){
	    		BadgeType type = (BadgeType)value;
	    		Dialogs.iconMessage("Badge - " + type.getAPIName(), 
	    				"Name: " + type.getName() + 
	    				"\nVersion: " + type.getVersion() + 
	    				"\nDescription: "+ type.getDescription() + 
	    				"\nShort Name: " + type.getBadge().getShortName(), 
	    				type.getAttribute().getIcon());
	    	}
	    	//Ban icon was clicked: Bans the user from chat
	    	else if(type == ClickableType.BAN){
	    		User user = (User)value;
	    		ModerationTools.ban(user);
	    	}
	    	//Timeout icon was clicked: Bans user for 10 minutes
	    	else if(type == ClickableType.TIMEOUT){
	    		User user = (User)value;
	    		ModerationTools.timeout(user, 600);
	    	}
	    }

	    //Click event performed
	    @Override
	    public void actionPerformed(ActionEvent e){
	        execute();
	    }
	}
}
