package twitch.message.types;

import twitch.connection.ChatConnection;
import twitch.gui.MutableAttributeSet;

/*
 * Broadcast message for standard logging (client-side messages)
 */
public class LogMessage extends BroadcastMessage{

	private MutableAttributeSet attribute;
	
	/*
	 * ChatConnection connection: Twitch channel the log occurred in
	 * long rawTime: The time the log occurred
	 * String message: The logged message
	 * MutableAttributeSet att: The attribute of the log
	 */
	public LogMessage(ChatConnection connection, long rawTime, String message, MutableAttributeSet att) {
		super(connection, rawTime, message);
		this.attribute = att;
	}
	
	public LogMessage(ChatConnection connection, String message, MutableAttributeSet att){
		this(connection, System.currentTimeMillis(), message, att);
	}
	
	@Override
	public void appendMessage(){
		getChatConnection().getLogger().onMessage(this);
		getChatConnection().getStyledChat().appendText("[" + super.getTime() + "] " + super.getMessage(), attribute, true);	
	}

}
