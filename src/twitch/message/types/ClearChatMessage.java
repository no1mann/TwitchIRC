package twitch.message.types;

import twitch.connection.ChatConnection;
import twitch.gui.Globals;

/*
 * Broadcast message for clearing the chat
 */
public class ClearChatMessage extends BroadcastMessage{

	public ClearChatMessage(ChatConnection connection, long time){
		super(connection, time);
		super.setMessage("Chat cleared by moderator. Prevented by " + Globals.NAME + ".");
	}
	
}
