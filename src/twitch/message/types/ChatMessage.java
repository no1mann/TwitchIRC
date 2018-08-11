package twitch.message.types;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import twitch.connection.ChatConnection;
import twitch.connection.TwitchChat;
import twitch.gui.AttributeList;
import twitch.gui.ClickableType;
import twitch.gui.MutableAttributeSet;
import twitch.gui.windows.Window;
import twitch.listeners.GlobalListeners;
import twitch.listeners.types.ListenerType;
import twitch.message.ModerationTools;
import twitch.message.emotes.ChatEmote;
import twitch.user.User;
import twitch.user.badge.BadgeType;
import twitch.utils.Formats;

/*
 * Standard chat message class
 */
public class ChatMessage implements Message, Comparable<Message>{
	
	//Used for denoting a /me command
	private static final String ME_CHAR = "ACTION ";
	
	private User user;
	private String message, time;
	private long rawTime;
	private Color color = Color.WHITE;
	private ArrayList<ChatEmote> emotes;
	private ChatConnection connection;
	
	/*
	 * ChatConnection connection: Twitch channel the message occurred in
	 * long rawTime: The time the message occurred
	 * User user: The user that sent the message
	 * String message: The message sent
	 * Color: The color the message sent (user for /me)
	 */
	public ChatMessage(ChatConnection connection, long rawTime, User user, String message, Color color){
		this(connection, rawTime, user, message);
		this.color = color;
	}
	
	/*
	 * ChatConnection connection: Twitch channel the message occurred in
	 * long rawTime: The time the message occurred
	 * User user: The user that sent the message
	 * String message: The message sent
	 */
	public ChatMessage(ChatConnection connection, long rawTime, User user, String message){
		this.connection = connection;
		this.user = user;
		this.message = message;
		this.rawTime = rawTime;
		this.time = Formats.getCurrentTimeStamp(rawTime);
		this.emotes = new ArrayList<ChatEmote>();
		if(user!=null){
			user.onMessage(this);
		}
	}
	
	//Prints the badges of a user
	public void printBadges(){
		//Prints the mod badge, and mod tools 
		if(ModerationTools.isMod(connection) && !ModerationTools.isNull(connection)){
			if(!user.isMod()){
				MutableAttributeSet att = AttributeList.getBanIconAttribute();
				att.setClickable(ClickableType.BAN, user);
				connection.getStyledChat().appendImage(att);
				connection.getStyledChat().appendSpace();
				
				att = AttributeList.getTimeoutIconAttribute();
				att.setClickable(ClickableType.TIMEOUT, user);
				connection.getStyledChat().appendImage(att);
				
				connection.getStyledChat().appendSpace();
			}
		}
		//Prints other badges user has (subscriber, prime, FFZ, others)
		for (BadgeType type : user.getTypes()) {
			//If option for showing image is enabled
			if(Window.showImages()){
				connection.getStyledChat().appendImage(type.getAttribute());
				connection.getStyledChat().appendSpace();
			}
			else{
				connection.getStyledChat().appendText("[" + type.getBadge().getShortName() + "] ", type.getBadge().getAttribute());
			}
		}
	}
	
	//Prints the username of the user
	public void printUsername(){
		MutableAttributeSet tempAtt;
		//If the user has custom attributes, use that attribute
		if(!user.hasUsernameAttribute()){
			tempAtt = AttributeList.getUsernameAttribute();
			tempAtt.setColor(user.getDisplayColor());
			tempAtt.setClickable(ClickableType.USERNAME, user.getUsername());
			user.setUsernameAttribute(tempAtt);
		}
		//User default attribute if user does not
		else
			tempAtt = user.getUsernameAttribute();
		//Appends the username
		connection.getStyledChat().appendText(user.getDisplayName(), tempAtt);
	}
	
	//Prints the message to the screen
	public void printMessage(){
		//MESSAGE ATTRIBUTES (/me or not)
		String printMessage = message;
		MutableAttributeSet messageAtt;
		//If /me, change the format
		if (getMessage().startsWith(ME_CHAR)) {
			messageAtt = AttributeList.getMessageAttribute();
			printMessage = message.substring(getMessage().indexOf(ME_CHAR) + ME_CHAR.length());
			messageAtt.setColor(user.getDisplayColor());
			messageAtt.setBold(true);
			connection.getStyledChat().appendText(" ", messageAtt);
		} 
		else{
			messageAtt = AttributeList.getMessageAttributeNoClone();
			connection.getStyledChat().appendText(": ", messageAtt);
		}
		
		//CHECKS FOR EMOTES
		//No emotes or images is disabled, just print the message
		if(emotes.size()==0 || !Window.showImages())
			connection.getStyledChat().appendText(printMessage, messageAtt);
		//If 1 or more emotes exist, print them
		else {
			//Sorts emotes by location in message
			Collections.sort(emotes);
			//For each emote in the message, split the message at that location, print first part of the message, than the emote, than the last part of the message (recursively)
			for (int i = 0; i < emotes.size(); i++) {
				// EMOTES
				ChatEmote emote = emotes.get(i);
				if(i==0 && emote.getStart()!=0)
					connection.getStyledChat().appendText(printMessage.substring(0, emote.getStart()), messageAtt);
				
				//If emote has not been loaded in, load it into memory
				if (!TwitchChat.emoteManager.hasEmote(emote.getEmoteType(), emote.getId())) {
					TwitchChat.emoteManager.loadEmote(emote.getEmoteType(), printMessage.substring(emote.getStart(), emote.getEnd()+1), emote.getId());
					TwitchChat.emoteManager.getEmote(emote.getEmoteType(), emote.getId()).setChannel("Global");
				}
				
				//Append the emote
				connection.getStyledChat().appendImage(TwitchChat.emoteManager.getEmote(emote.getEmoteType(), emote.getId()).getAttributeSet());

				// MESSAGE
				
				//If last emote, print the end part of the message
				if (i + 1 == emotes.size()) {
					if(emote.getEnd() > printMessage.length())
						break;
					connection.getStyledChat().appendText(printMessage.substring(emote.getEnd() + 1), messageAtt);
				} 
				//If not the last emote, split up the last part of the message into the new emote
				else {
					if(emote.getEnd() > printMessage.length() || emotes.get(i + 1).getStart() > printMessage.length())
						break;
					connection.getStyledChat().appendText(printMessage.substring(emote.getEnd() + 1, emotes.get(i + 1).getStart()), messageAtt);
				}
			}
		}
	}
	
	//Prints the time to the screen
	public static void printTime(ChatConnection connection){
		connection.getStyledChat().appendText(Formats.getCurrentTimeStamp(System.currentTimeMillis()) +  " ", AttributeList.getTimeAttribute());
	}
	
	//Prints the entire message
	public void appendMessage() {
		// TIME
		printTime(connection);

		// BADGES
		printBadges();

		// USERNAME
		printUsername();

		// MESSAGE
		printMessage();

		connection.getStyledChat().newLine();
	}

	public void addEmote(ChatEmote emote){
		emotes.add(emote);
	}
	
	public void addEmotes(Collection<ChatEmote> emotes){
		for(ChatEmote emote : emotes)
			addEmote(emote);
	}
	
	public String toString(){
		return "[" + time + "] " + user.getDisplayName() + ": " + message;
	}
	
	public boolean equals(ChatMessage message){
		return message.toString().equals(this.toString());
	}
	
	public int hashCode(){
		return message.hashCode() + time.hashCode() + user.hashCode();
	}
	
	public User getSender(){
		return user;
	}
	
	public String getMessage(){
		return message;
	}
	
	public String getTime(){
		return time;
	}
	
	public Color getColor(){
		return color;
	}

	@Override
	public long getRawTime() {
		return rawTime;
	}

	@Override
	public int compareTo(Message o) {
		return Long.compare(rawTime, o.getRawTime());
	}

	@Override
	public void callListeners() {
		GlobalListeners.triggerListener(ListenerType.ON_USER_MESSAGE, this);
	}

	@Override
	public ChatConnection getChatConnection() {
		return connection;
	}
	
}
