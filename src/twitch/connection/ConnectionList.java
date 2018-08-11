package twitch.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.esotericsoftware.minlog.Log;

import twitch.channels.StreamListener;
import twitch.gui.windows.Window;
import twitch.utils.Formats;

/*
 * Stores and manages all current channel connections
 */
public class ConnectionList{
	
	/*
	 * Stores all channel connections:
	 * Key : Channel name
	 * Value : Channel connection object
	 */
	private static ConcurrentHashMap<String, ChatConnection> connections = new ConcurrentHashMap<String, ChatConnection>();
	private static ChatConnection[] channelList = new ChatConnection[0];
	
	//Generates a new chat connection and tracks its updates
	public static void setupNewConnection(String channel, boolean connect){
		ChatConnection con;
		//If channel already exists, connect to it
		if(!ConnectionList.containsChannel(channel))
			con = Connection.connect(channel.toLowerCase(), connect);
		else{
			//Generate new connection
			con = ConnectionList.getChannel(channel);
			if(connect)
				con.connect();
			
		}
		//If channel didn't fail to connect
		if(con != null){
			StreamListener.addChannel(channel);
			ConnectionList.addChannel(con);
			if(connect)
				Window.changeChannels(con.getChannelName());
		}
	}
	
	//Gets the first connected channel for default information
	public static String getFirstConnectedChannel(){
		for(ChatConnection connect : ConnectionList.getConnections())
			if(connect.isConnected() && StreamListener.getStreamData(connect).isLive())
				return connect.getChannelName();
			
		for(ChatConnection connect : ConnectionList.getConnections())
			if(connect.isConnected())
				return connect.getChannelName();
			
		return null;
	}

	
	//Adds channel to list
	public static synchronized void addChannel(ChatConnection connection){
		connections.put(Formats.formatChannel(connection.getChannelName()), connection);
		Log.debug("Channel (" + connection.getChannelName() + ") added to connection list");
		updateList();
	}
	
	public static synchronized ChatConnection getChannel(String channel){
		return connections.get(Formats.formatChannel(channel));
	}
	
	public static synchronized boolean containsChannel(String channel){
		return connections.containsKey(Formats.formatChannel(channel));
	}
	
	public static synchronized boolean containsChannel(ChatConnection channel){
		return connections.containsValue(channel);
	}
	
	public static synchronized ChatConnection removeChannel(ChatConnection connection){
		return removeChannel(Formats.formatChannel(connection.getChannelName()));
	}
	
	public static synchronized ChatConnection removeChannel(String channel){
		ChatConnection current = connections.remove(Formats.formatChannel(channel));
		Log.debug("Channel (" + channel + ") removed from connection list");
		updateList();
		return current;
	}
	
	//Updates all chat connections and organizes them alphabetically
	private static synchronized void updateList(){
		List<ChatConnection> list = new ArrayList<ChatConnection>();
		list.addAll(connections.values());
		Collections.sort(list);
		channelList = new ChatConnection[list.size()];
		channelList = list.toArray(channelList);
		Log.debug("Connection list updated");
	}
	
	public static synchronized Collection<ChatConnection> getConnections(){
		return connections.values();
	}
	
	public static synchronized Collection<String> getChannelNames(){
		return connections.keySet();
	}
	
	public static synchronized ChatConnection[] getChannelArray(){
		return channelList;
	}
	
	//Remove all chat connections
	public static synchronized void clearChannels(){
		for(ChatConnection connect : connections.values())
			if(connect.isConnected())
				connect.disconnect();
			
		
		connections.clear();
	}
}
