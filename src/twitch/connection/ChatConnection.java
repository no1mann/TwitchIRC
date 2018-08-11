package twitch.connection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.SwingUtilities;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import com.esotericsoftware.minlog.Log;

import twitch.channels.ChannelInfo;
import twitch.gui.Globals;
import twitch.gui.ScrollWindow;
import twitch.gui.TwitchChatDocument;
import twitch.gui.windows.Window;
import twitch.listeners.GlobalListeners;
import twitch.listeners.types.ListenerType;
import twitch.message.ChatColor;
import twitch.message.MessageProcessor;
import twitch.message.types.Message;
import twitch.user.User;
import twitch.user.UserList;
import twitch.user.badge.BadgeManager;
import twitch.user.badge.BadgeType;
import twitch.user.badge.GlobalBadges;
import twitch.utils.Dialogs;
import twitch.utils.LoopingQueue;

/*
 * Stores all information involving chat information and channel information
 */
public class ChatConnection implements Comparable<ChatConnection>{
	
	//Number of instances of chat connections - Used to improve performance that Twitch patched. Keep at 1
	public static final int INSTANCES = 1;
	//Used for preventing global ban
	public static final int MESSAGE_DELAY = 1;
	
	private static final Object CONNECT_LOCK = new Object();
	private static final String CHARSET = "UTF-8";
	public static BadgeManager badgeList;
	private static LoopingQueue<TwitchUser> chatQueue = new LoopingQueue<TwitchUser>();
	private static TwitchChat twitchChat = new TwitchChat();
	private static Thread initThread;
	
	private User logger;
	
	private ChatConnection connection = this;
	private MessageProcessor messageProcessor;
	private LoopingQueue<String> sentMessageHistory;
	private LinkedList<String> chatLog;
	private String channelName, username;
	private ChannelInfo channelInfo;
	private TwitchChatDocument document;
	private boolean connected = false;
	private static boolean failed = false;
	
	private Thread startThread;
	private HashSet<ScrollWindow> scroll;
	
	//Initializes connections to Twitch IRC chat
	static{
		initThread = new Thread(new Runnable(){
			@Override
			public void run() {
				new UserList();
				badgeList = new BadgeManager();
				
				//Loads arrays to track connections
				ConnectionThread[] listCon = new ConnectionThread[INSTANCES];
				Thread[] list = new Thread[INSTANCES];
				Log.info("Starting connection threads");
				//Starts all connection threads
				for(int i = 0; i < INSTANCES; i++){
					listCon[i] = new ConnectionThread();
					list[i] = new Thread(listCon[i], "ChatConnectionThread");
					list[i].start();
				}
				Log.info("Waiting for connections...");
				//Waits for all connections to complete
				for(int i = 0; i < INSTANCES; i++){
					try {
						list[i].join();
					} catch (InterruptedException e) {}
					if(listCon[i].getUser() == null){
						for(int j = 0; j < INSTANCES; j++){
							if(listCon[j].getUser() != null)
								listCon[j].getUser().disconnect();
						}
						failed = true;
						break;
					}
					chatQueue.add(listCon[i].getUser());
				}
				//Initializes chat connection for receiving messages from other users
				twitchChat = new TwitchChat();
				try {
					twitchChat.setEncoding(CHARSET);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				try {
					twitchChat.connect(Globals.IRC_DOMAIN, Globals.IRC_PORT, "oauth:" + Globals.TOKEN);
				} catch (Exception e){
					parseConnectionError(e);
					failed = true;
				}
			}	
		}, "ConnectionInitiationThread");
		initThread.start();
	}
	
	public ChatConnection(){}
	
	/*
	 * Main Constructor:
	 * Channel - Channel name
	 * Scroll - Chat window (Main GUI or Pop out GUI instance)
	 */
	public ChatConnection(final String channel, ScrollWindow scroll){
		Log.info("Connection to " + channel + " instantiated");
		this.channelName = channel;
		this.username = channelName;
		if(channelName.startsWith("#"))
			this.username = channelName.substring(1);
		this.scroll = new HashSet<ScrollWindow>();
		this.scroll.add(scroll);
		this.sentMessageHistory = new LoopingQueue<String>();
		this.messageProcessor = new MessageProcessor(this);
		this.chatLog = new LinkedList<String>();
		
		//Loads channel info
		startThread = new Thread(new Runnable(){
			@Override
			public void run() {
				channelInfo = new ChannelInfo(channelName.substring(1));
				if (!channelInfo.loadedSuccessfully()) 
					channelInfo = null;
				try {
					initThread.join();
				} catch (InterruptedException e) {}
				badgeList.loadChannelBadges(channelInfo);
				if(!badgeList.loadedSuccesfully())
					Window.setShowImages(false);
				UserList.addConnection(connection);
			}
		}, "ConnectionStartThread");
		startThread.start();
	}
	
	//Connects all chat instances too this channel
	public void connect(){
		Log.info("Starting connection to " + channelName);
		try {
			initThread.join();
		} catch (InterruptedException e) {}
		this.document = new TwitchChatDocument();
		TwitchChat.emoteManager.loadChannel(username);
		for(TwitchUser user : chatQueue)
			user.joinChannel(channelName);
		twitchChat.joinChannel(channelName);
		//Twitch configuration commands
		twitchChat.sendRawLine("CAP REQ :twitch.tv/tags");			//Load chat messages as JSON Objects
		twitchChat.sendRawLine("CAP REQ :twitch.tv/commands");		//Twitch command listener
		twitchChat.sendRawLine("CAP REQ :twitch.tv/membership");	//Sub messages
		logger = new User(this, "logger", 100);
		try {
			startThread.join();
		} catch (InterruptedException e) {}
		Log.info("Connection to " + channelName + " was successful");
		connected = true;
		GlobalListeners.triggerListener(ListenerType.ON_CHANNEL_JOIN, this);
	}
	
	//Chat connection thread for sending messages
	public static class ConnectionThread implements Runnable{

		private TwitchUser user;
		
		public ConnectionThread(){}
		
		@Override
		public void run() {
			user = generateNewUser();
		}
		
		//Instantiates new TwitchUser
		private TwitchUser generateNewUser(){
			TwitchUser user = new TwitchUser(Globals.USERNAME);
			try {
				user.setEncoding(CHARSET);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			try {
				user.connect(Globals.IRC_DOMAIN, Globals.IRC_PORT, "oauth:" + Globals.TOKEN);
			} catch (Exception e){
				synchronized(CONNECT_LOCK){
					if(!failed){
						failed = true;
						parseConnectionError(e);
					}
				}
				return null;
			}
			user.setMessageDelay(MESSAGE_DELAY);
			return user;
		}
		
		public TwitchUser getUser(){
			return user;
		}

	}
	
	//Parses error messages
	private static synchronized void parseConnectionError(final Exception e){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String message = null;
				if (e instanceof IOException) 
					message =  "Error: Cannot connect to Twitch IRC server.\n" + "IRC Domain: " + Globals.IRC_DOMAIN
							+ "\n" + "IRC Port: " + Globals.IRC_PORT + "\n\n" + e.getMessage();
				else if (e instanceof NickAlreadyInUseException)
					message = "Error: Nickname already in use.\n\n" + e.getMessage();
				else if (e instanceof IrcException)
					message = "Error: Cannot join server.\n" + "IRC Domain: " + Globals.IRC_DOMAIN + "\n"
							+ "IRC Port: " + Globals.IRC_PORT + "\n\n";
				else 
					message = "Error: Unknown Error.\n\n";
				Log.error(message);
				Dialogs.error("Error", message);
			}
		});
	}
	
	//Logs chat message
	public void logMessage(Message message){
		chatLog.addLast(message.toString());
	}
	
	public LinkedList<String> getChatLog(){
		return chatLog;
	}
	
	//Adds most recent sent message to the message history
	public void addLastMessage(String message){
		sentMessageHistory.resetLoop();
		if(sentMessageHistory.preview() != null && message.equals(sentMessageHistory.previewReverse()))
			return;
		sentMessageHistory.add(message);
		sentMessageHistory.backup();
	}
	
	//Post message to this channel
	public void sendMessage(String message){
		if(message == null || message.equals("") || message.trim().length() == 0)
			return;
		//Gets most recent instance of the Twitch user
		TwitchUser current = this.popUser();
		if(!message.startsWith("/") || message.startsWith("/me"))
			ChatColor.onChatMessage();
		current.postInChat(this.getChannelName(), message);
	}
	
	public String getChannelName(){
		return channelName;
	}
	
	public TwitchUser popUser(){
		return chatQueue.get();
	}
	
	public boolean isConnected(){
		return connected;
	}
	
	public BadgeType getBadge(String name, String version){
		if(badgeList == null || !badgeList.hasBadge(channelInfo.getChannel(), name, version)){
			return null;
		}
		return badgeList.getBadge(channelInfo.getChannel(), name, version);
	}
	
	public BadgeType getBadge(GlobalBadges badge){
		return badgeList.getBadge(channelInfo.getChannel(), badge);
	}
	
	public Collection<BadgeType> getBadgeList(){
		if(badgeList == null){
			return null;
		}
		return badgeList.getBadgeList();
	}
	
	public LoopingQueue<String> getMessageHistory(){
		return sentMessageHistory;
	}
	
	public LoopingQueue<TwitchUser> getChatQueue(){
		return chatQueue;
	}
	
	public TwitchChatDocument getStyledChat(){
		return document;
	}
	
	public MessageProcessor getMessageProcessor(){
		return messageProcessor;
	}
	
	public void disconnect(){
		Log.info("Disconnecting from " + channelName);
		connected = false;
		twitchChat.partChannel(channelName);
		for(TwitchUser user : chatQueue){
			user.partChannel(channelName);
		}
		Log.info("Succesfully disconnected from " + channelName);
	}
	
	public User getLogger(){
		return logger;
	}

	@Override
	public int compareTo(ChatConnection val) {
		return channelName.compareTo(val.getChannelName());
	}
	
	public void scroll(){
		for(ScrollWindow scr : scroll){
			scr.updateScrollBar(this);
		}
	}
	
	public void addScrollBar(ScrollWindow window){
		this.scroll.add(window);
		window.updateScrollBar(this);
	}
	
	public void removeScrollBar(ScrollWindow window){
		this.scroll.remove(window);
		window.updateScrollBar(this);
	}
	
	public String toString(){
		return channelName;
	}
	
	public String getUsername(){
		return username;
	}
	
	public ChannelInfo getChannelInfo(){
		return channelInfo;
	}
}
