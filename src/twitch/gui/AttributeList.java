package twitch.gui;

import java.awt.Color;

import com.esotericsoftware.minlog.Log;

import twitch.connection.URLLoader;

/*
 * 
 * Color and design attributes for chat window
 * 
 * Creates single static instances of the attributes for memory efficiency
 * 
 */
public class AttributeList {

	private static MutableAttributeSet defaultAtt;
	
	//Chat Attributes
	private static MutableAttributeSet timeAtt;
	private static MutableAttributeSet imageAtt;
	private static MutableAttributeSet bitsAtt;
	private static MutableAttributeSet usernameAtt;
	private static MutableAttributeSet messageAtt;
	private static MutableAttributeSet broadcastAtt;
	
	//Message Attributes
	private static MutableAttributeSet joinChannelAtt;
	private static MutableAttributeSet leaveChannelAtt;
	private static MutableAttributeSet logAtt;
	
	//Other
	private static MutableAttributeSet banIconAtt;
	private static MutableAttributeSet timeoutIconAtt;
	
	//Initializes all the default attributes
	static {
		Log.info("Loading Attributes");
		defaultAtt = new MutableAttributeSet();
		defaultAtt.setColor(Color.WHITE);
		defaultAtt.setFontSize(14);
		
		timeAtt = (MutableAttributeSet) defaultAtt.clone();
		timeAtt.setColor(Color.GRAY);
		timeAtt.setFontSize(12);
		
		banIconAtt = (MutableAttributeSet) defaultAtt.clone();
		banIconAtt.setImage(URLLoader.getImageFromFile(Globals.BAN_ICON));
		
		timeoutIconAtt = (MutableAttributeSet) defaultAtt.clone();
		timeoutIconAtt.setImage(URLLoader.getImageFromFile(Globals.TIMEOUT_ICON));
		
		imageAtt = (MutableAttributeSet) defaultAtt.clone();
		
		bitsAtt = (MutableAttributeSet) defaultAtt.clone();
		bitsAtt.setBold(true);
		
		usernameAtt = (MutableAttributeSet) defaultAtt.clone();
		usernameAtt.setBold(true);
		
		messageAtt = (MutableAttributeSet) defaultAtt.clone();
		
		broadcastAtt = (MutableAttributeSet) defaultAtt.clone();
		broadcastAtt.setBold(true);
		broadcastAtt.setColor(Color.LIGHT_GRAY);
		
		joinChannelAtt = (MutableAttributeSet) defaultAtt.clone();
		joinChannelAtt.setColor(new Color(0, 193, 15));
		joinChannelAtt.setBold(true);
		
		leaveChannelAtt = (MutableAttributeSet) defaultAtt.clone();
		leaveChannelAtt.setColor(new Color(219, 0, 0));
		leaveChannelAtt.setBold(true);
		
		logAtt = (MutableAttributeSet) defaultAtt.clone();
		logAtt.setColor(new Color(219, 0, 0));
		logAtt.setBold(true);
		logAtt.setUnderlined(true);
	}
	
	public static MutableAttributeSet getBanIconAttribute(){
		return (MutableAttributeSet) banIconAtt;
	}
	
	public static MutableAttributeSet getTimeoutIconAttribute(){
		return (MutableAttributeSet) timeoutIconAtt;
	}
	
	public static MutableAttributeSet getJoinChannelAttribute(){
		return (MutableAttributeSet) joinChannelAtt;
	}
	
	public static MutableAttributeSet getLeaveChannelAttribute(){
		return (MutableAttributeSet) leaveChannelAtt;
	}
	
	public static MutableAttributeSet getBitsAttribute(){
		return (MutableAttributeSet) bitsAtt.clone();
	}
	
	public static MutableAttributeSet getLogAttribute(){
		return (MutableAttributeSet) logAtt.clone();
	}
	
	public static MutableAttributeSet getDefaultAttribute(){
		return (MutableAttributeSet) defaultAtt.clone();
	}
	
	public static MutableAttributeSet getDefaultAttributeNoClone(){
		return (MutableAttributeSet) defaultAtt;
	}
	
	public static MutableAttributeSet getTimeAttribute(){
		return (MutableAttributeSet) timeAtt.clone();
	}
	
	public static MutableAttributeSet getUsernameAttribute(){
		return (MutableAttributeSet) usernameAtt.clone();
	}
	
	public static MutableAttributeSet getMessageAttribute(){
		return (MutableAttributeSet) messageAtt.clone();
	}
	
	public static MutableAttributeSet getMessageAttributeNoClone(){
		return (MutableAttributeSet) messageAtt;
	}
	
	public static MutableAttributeSet getBroadcastAttribute(){
		return (MutableAttributeSet) broadcastAtt.clone();
	}
	
	public static MutableAttributeSet getImageAttribute(){
		return (MutableAttributeSet) imageAtt.clone();
	}
}
