package twitch.listeners.types;

import twitch.connection.ChatConnection;

public interface ChannelListener {

	public void onChannelJoin(ChatConnection channel);
	public void onUserListReload();
	public void onChannelLeave(String channel);
	
}
