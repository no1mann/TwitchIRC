package twitch.listeners;

import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;

import twitch.connection.ChatConnection;
import twitch.listeners.types.ChannelListener;
import twitch.listeners.types.CommandListener;
import twitch.listeners.types.ListenerData;
import twitch.listeners.types.ListenerType;
import twitch.listeners.types.MessageListener;
import twitch.listeners.types.WindowListener;
import twitch.message.types.BroadcastMessage;
import twitch.message.types.ChatMessage;

/*
 * Listener manager:
 * 
 * Listeners allow for tracking custom events such as window events, chat message events, channel events, and more. Future API use.
 */
public class GlobalListeners {

	//New event triggers get stored here and get processed by the ListenerProcessThread
	private static LinkedBlockingQueue<ListenerData> processList;
	
	private static boolean enableListeners = true;
	
	//Stores different listeners
	private static HashSet<MessageListener> messageListeners = new HashSet<MessageListener>();
	private static HashSet<ChannelListener> channelListeners = new HashSet<ChannelListener>();
	private static HashSet<WindowListener> windowListeners = new HashSet<WindowListener>();
	private static HashSet<CommandListener> commandListeners = new HashSet<CommandListener>();
	
	static{
		processList = new LinkedBlockingQueue<ListenerData>();
		//Starts listener thread
		Thread thread = new Thread(new ListenerProcessThread(), "ListenerProcessorThread");
		thread.start();
	}
	
	/*
	 * ADDS LISTENERS
	 */
	
	public static void addMessageListener(MessageListener lis){
		if(lis != null)
			messageListeners.add(lis);
	}
	
	public static void addChannelListener(ChannelListener lis){
		if(lis != null)
			channelListeners.add(lis);
	}
	
	public static void addWindowListener(WindowListener lis){
		if(lis != null)
			windowListeners.add(lis);
	}
	
	public static void addCommandListener(CommandListener lis){
		if(lis != null)
			commandListeners.add(lis);
	}
	
	/*
	 * REMOVES LISTENERS
	 */
	
	public static void removeMessageListener(MessageListener lis){
		if(lis != null)
			messageListeners.remove(lis);
	}
	
	public static void removeChannelListener(ChannelListener lis){
		if(lis != null)
			channelListeners.remove(lis);
	}
	
	public static void removeWindowListener(WindowListener lis){
		if(lis != null)
			windowListeners.remove(lis);
	}
	
	public static void removeCommandListener(CommandListener lis){
		if(lis != null)
			commandListeners.remove(lis);
	}
	
	//Adds a new trigger to the process list
	public static void triggerListener(final ListenerType type, final Object data) {
		processList.add(new ListenerData(type, data));
	}
	
	//Processes a single triggered event
	private static void trigger(ListenerData lisData) {
		ListenerType type = lisData.getType();
		Object data = lisData.getData();
		
		//Commands get overridden for enabling/disabling triggers
		if (type == ListenerType.ON_COMMAND) {
			for (CommandListener lis : commandListeners)
				lis.onCommand((CommandEvent) data);
			return;
		}
		
		//If listeners aren't enabled, ignore
		if (!enableListeners)
			return;
		
		/*
		 * Processes listener
		 */
		if (type == ListenerType.ON_USER_MESSAGE)
			for (MessageListener lis : messageListeners)
				lis.onChatMessage((ChatMessage) data);

		else if (type == ListenerType.ON_BROADCAST_MESSAGE)
			for (MessageListener lis : messageListeners)
				lis.onBroadcastMessage((BroadcastMessage) data);

		else if (type == ListenerType.ON_SHUTDOWN)
			for (WindowListener lis : windowListeners)
				lis.onShutdown();

		else if (type == ListenerType.ON_STARTUP)
			for (WindowListener lis : windowListeners)
				lis.onStartup();

		else if (type == ListenerType.ON_USER_LIST_RELOAD)
			for (ChannelListener lis : channelListeners)
				lis.onUserListReload();

		else if (type == ListenerType.ON_CHANNEL_JOIN)
			for (ChannelListener lis : channelListeners)
				lis.onChannelJoin((ChatConnection) data);

		else if (type == ListenerType.ON_CHANNEL_LEAVE)
			for (ChannelListener lis : channelListeners)
				lis.onChannelLeave((String) data);

	}
	
	//Toggles listeners
	public static void enableListeners(boolean val){
		enableListeners = val;
	}
	
	/*
	 * Processes all events in the queue in own thread
	 */
	private static class ListenerProcessThread implements Runnable{
		@Override
		public void run() {
			while(true){
				//If the queue is not empty, process everything in it
				if(!processList.isEmpty()){
					for(ListenerData mes : processList)
						trigger(mes);
					processList.clear();
				}
				//Wait 50 milliseconds to check for triggers
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {}
			}
		}
	}
}
