package twitch.message.types;

import java.awt.Color;

import twitch.connection.ChatConnection;
import twitch.gui.AttributeList;
import twitch.gui.MutableAttributeSet;
import twitch.listeners.GlobalListeners;
import twitch.listeners.types.ListenerType;
import twitch.user.User;
import twitch.utils.Formats;

/*
 * Standard broadcast message for announcement chat messages or client-side messages
 */
public class BroadcastMessage implements Message, Comparable<Message>{

	private String time, message;
	private long rawTime;
	private Color color = Color.GRAY, bgColor = null;
	private ChatConnection connection;
	
	/*
	 * ChatConnection connection: Twitch channel the broadcast occurred
	 * long rawTime: The time the broadcast occurred
	 * String message: The broadcast message 
	 */
	public BroadcastMessage(ChatConnection connection, long rawTime, String message){
		this.rawTime = rawTime;
		this.time = Formats.getCurrentTimeStamp(rawTime);
		this.message = message;
		this.connection = connection;
	}
	
	public BroadcastMessage(ChatConnection connection, long time){
		this(connection, time, null);
	}
	
	//Standard broadcast message appension (time, color, background)
	@Override
	public void appendMessage() {
		connection.getLogger().onMessage(this);
		MutableAttributeSet att = AttributeList.getBroadcastAttribute();
		att.setColor(color);
		if(bgColor != null)
			att.setBackground(bgColor);
		ChatMessage.printTime(connection);
		connection.getStyledChat().appendText(message, att, true);
	}
	
	public void setMessage(String message){
		this.message = message;
		if(this.message.startsWith(":"))
			this.message = this.message.substring(1);
		else if(this.message.startsWith(" :"))
			this.message = this.message.substring(2);
	}
	
	public void setColor(Color color){
		this.color = color;
	}
	
	public void setBackgroundColor(Color color){
		this.bgColor = color;
	}
	
	@Override
	public User getSender() {
		return connection.getLogger();
	}

	@Override
	public String getTime() {
		return time;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public long getRawTime() {
		return rawTime;
	}

	public String toString(){
		return "[" + time + "] " + message;
	}
	
	@Override
	public int compareTo(Message o) {
		return Long.compare(rawTime, o.getRawTime());
	}

	@Override
	public void callListeners() {
		GlobalListeners.triggerListener(ListenerType.ON_BROADCAST_MESSAGE, this);
	}

	@Override
	public ChatConnection getChatConnection() {
		return connection;
	}

}
