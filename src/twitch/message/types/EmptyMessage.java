package twitch.message.types;

import twitch.connection.ChatConnection;

/*
 * Empty broadcast message for simple new line printing
 */
public class EmptyMessage extends BroadcastMessage{

	public EmptyMessage(ChatConnection connection) {
		super(connection, System.currentTimeMillis(), "\n");
	}
	
	public void appendMessage(){
		getChatConnection().getStyledChat().newLine();
	}

}
