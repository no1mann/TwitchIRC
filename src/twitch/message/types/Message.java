package twitch.message.types;

import java.awt.Color;

import twitch.connection.ChatConnection;
import twitch.user.User;

public interface Message {
	
	public User getSender();
	public String getTime();
	public long getRawTime();
	public String getMessage();
	public Color getColor();
	public void appendMessage();
	public void callListeners();
	public ChatConnection getChatConnection();
	
}
