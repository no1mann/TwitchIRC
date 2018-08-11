package twitch.channels;

import java.util.concurrent.ConcurrentHashMap;

import twitch.connection.ChatConnection;
import twitch.connection.ConnectionList;
import twitch.gui.windows.Window;
import twitch.utils.Formats;

/*
 * Tracks live stream updates
 */
public class StreamListener {
	
	/*
	 * Stores all stream data - Thread safe
	 * Key : Channel Username
	 * Value : StreamInfo Object
	 */
	private static ConcurrentHashMap<String, StreamInfo> streamData = new ConcurrentHashMap<String, StreamInfo>();
	
	//Initializes new stream update thread
	static{
		new Thread(new Runnable(){
			@Override
			public void run() {
				init();
			}
		}, "StreamDataThread").start();
	}
	
	//Adds all channels from the current connection list
	private static void init(){
		for(String channel : ConnectionList.getChannelNames()){
			addChannel(channel);
		}
		runPolls();
	}

	/*
	 * Polls the Twitch API for stream updates -
	 * Checks for updates every 10 seconds
	 */
	private static void runPolls() {
		//StreamListenerPoll Thread initalizes - Fetches updates from Twitch API for stream data
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					//Cycles through all connected channels
					for (String channel : ConnectionList.getChannelNames()) {
						channel = Formats.formatChannel(channel);
						// Channel is not new
						if (streamData.containsKey(channel)) {
							streamData.get(channel).reloadData();
							checkUpdates(channel);
						} else 
							addChannel(channel);
					}
					//Pause thread for 10 seconds
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {}
				}
			}
		}, "StreamListenerPoll").start();
	}
	
	//Adds new channel too stream listener thread
	public static void addChannel(String chann){
		final String channel = Formats.formatChannel(chann);
		new Thread(new Runnable(){
			@Override
			public void run() {
				//Adds to main data structure
				streamData.put(channel, new StreamInfo(channel));
				//Update GUI
				Window.refreshChannelList();
			}
		}, "NewChannelThread").start();
	}
	
	//Updates GUI information about stream updates
	private static void checkUpdates(String channel){
		Window.setConnectedText();
		Window.refreshChannelList();
	}
	
	public static StreamInfo getStreamData(String channel){
		if(streamData == null)
			return null;
		return streamData.get(Formats.formatChannel(channel));
	}
	
	public static StreamInfo getStreamData(ChatConnection channel){
		return getStreamData(channel.getChannelName());
	}
	
	public static StreamInfo getStreamData(ChannelInfo channel){
		return getStreamData(channel.getChannel());
	}
}
