package twitch.connection;

import twitch.gui.windows.Window;
import twitch.listeners.GlobalListeners;
import twitch.listeners.types.ListenerType;
import twitch.utils.Dialogs;

/*
 * Sets up channel connections
 */
public class Connection {
	
	/*
	 * Generates a new chat connection
	 * String channel : channel username
	 * boolean connect : connect to the channel?
	 */
	public static ChatConnection connect(String channel, boolean connect){
		if(channel.charAt(0)!='#')
			channel = "#" + channel;
		ChatConnection connection = new ChatConnection(channel, Window.window);
		if(connect){
			connection.connect();
			//Failed to connect, return null
			if(!connection.isConnected())
				return null;
		}
		return connection;
	}
	
	//Disconnects from chat
	public static void disconnect(ChatConnection connection){
		if(connection == null)
			return;
		String channel = Window.getCurrentConnection().getChannelName();
		GlobalListeners.triggerListener(ListenerType.ON_CHANNEL_LEAVE, channel);
		connection.disconnect();
	}
	
	//Opens new chat connection dialog
	public static void openJoinChannel(){
		String channelT = Dialogs.input("Join a Channel", "Enter a channel to join:");
		if(channelT==null || channelT.equals(""))
			return;
		ConnectionList.setupNewConnection(channelT, true);
	}
	
	//Opens new chat information dialog
	public static void openAddChannel(){
		String channelT = Dialogs.input("Add a Channel", "Enter a channel to add to the list:");
		if(channelT==null || channelT.equals("")){
			return;
		}
		ConnectionList.setupNewConnection(channelT, false);
	}
	
}
