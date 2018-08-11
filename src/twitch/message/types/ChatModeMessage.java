package twitch.message.types;

import twitch.connection.ChatConnection;
import twitch.message.ChatMode;

/*
 * Broadcast message for a change in chat mode (sub mode, r9k, follow, etc.)
 */
public class ChatModeMessage extends BroadcastMessage{

	private ChatMode type;
	private boolean enabled;
	
	/*
	 * ChatConnection connection: Twitch channel the broadcast occurred
	 * long rawTime: The time the broadcast occurred
	 * ChatMode type: The type of chat mode
	 * String message: The broadcast message 
	 * boolean enabled: The toggle of the chat mode (enabled or disabled)
	 */
	public ChatModeMessage(ChatConnection connection, long time, ChatMode type, String message, boolean enabled){
		super(connection, time);
		this.type = type;
		this.setMessage(message);
		this.enabled = enabled;
	}
	
	public ChatMode getChatMode(){
		return type;
	}
	
	public boolean wasEnabled(){
		return enabled;
	}
}
