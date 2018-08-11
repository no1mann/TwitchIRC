package twitch.listeners.types;

import twitch.message.types.BroadcastMessage;
import twitch.message.types.ChatMessage;

public interface MessageListener {
	
	public void onChatMessage(final ChatMessage message);
	public void onBroadcastMessage(final BroadcastMessage message);
	
}
