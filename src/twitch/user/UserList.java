package twitch.user;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;

import twitch.connection.ChatConnection;
import twitch.connection.URLLoader;
import twitch.gui.Globals;
import twitch.gui.windows.Window;
import twitch.listeners.GlobalListeners;
import twitch.listeners.types.ListenerType;
import twitch.user.badge.BadgeManager;
import twitch.user.badge.GlobalBadges;

/*
 * Global user list for tracking users of all channels
 */
@SuppressWarnings("unchecked")
public class UserList {
	
	//Users who get the dev badge
	public static final String[] ADMINS = {"no1mann", "no2mann"};
	
	/*
	 * HashMap for all user instances:
	 * Key: The chat connection of the user list you want
	 * Value: A HashMap of users (key: username, value: user instance)
	 */
	private static ConcurrentHashMap<ChatConnection, ConcurrentHashMap<String, User>> userList;
	//HashMap of locks for channel concurrency
	private static ConcurrentHashMap<ChatConnection, Object> lockList;
	
	/*
	 * Initiates all data structures and the thread for checking for user list updates
	 */
	static{
		userList = new ConcurrentHashMap<ChatConnection, ConcurrentHashMap<String, User>>();
		lockList = new ConcurrentHashMap<ChatConnection, Object>();
		//User List Update Thread
		new Thread(new Runnable(){
			public void run() {
				while(true){
					try{
						//Cycles through every user list and updates them
						for(ChatConnection connection : userList.keySet()){
							if(connection.isConnected()){
								loadUserList(connection);
								Thread.sleep(1000);
							}
						}
					} catch(Exception e){}
					System.gc();
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e1) {}
				}
			}
		}, "UserListUpdateThread").start();;
	}
	
	//Loads the user list of a specific channel
	private static void loadUserList(ChatConnection connection) {
		synchronized (lockList.get(connection)) {
			//Old User List
			ConcurrentHashMap<String, User> currentUserList = userList.get(connection);
			//New User List
			ConcurrentHashMap<String, User> data = new ConcurrentHashMap<String, User>();
			fetchData(connection, data);
			for (String username : data.keySet()) {
				if (!currentUserList.containsKey(username))
					currentUserList.put(username, data.get(username));
				currentUserList.get(username).setConnected(true);
			}
			for (String username : currentUserList.keySet())
				if (!data.containsKey(username))
					currentUserList.get(username).setConnected(false);
			
			// Checks for Custom Badges
			// Broadcaster
			if (currentUserList.containsKey(connection.getUsername().toLowerCase())
					&& !currentUserList.get(connection.getUsername().toLowerCase()).isType(GlobalBadges.BROADCASTER))
				currentUserList.get(connection.getChannelInfo().getChannel().toLowerCase()).addType(connection.getBadge(GlobalBadges.BROADCASTER));
			
			// Admins
			for (String name : ADMINS)
				if (currentUserList.containsKey(name)) 
					currentUserList.get(name).addType(connection.getBadge(BadgeManager.APP_ADMIN_NAME, BadgeManager.APP_ADMIN_VERSION));

			data.clear();
			data = null;
			//Event listener
			GlobalListeners.triggerListener(ListenerType.ON_USER_LIST_RELOAD, null);
			if (connection.equals(Window.getCurrentConnection()))
				Window.updateUserList();
		}
	}
	
	//Creates a new user list to keep track of
	public static void addConnection(ChatConnection connection){
		userList.put(connection, new ConcurrentHashMap<String, User>());
		lockList.put(connection, new Object());
		new Thread(new Runnable(){
			@Override
			public void run() {
				loadUserList(connection);
			}
		}, "UserListLoadThread").start();
	}
	
	public static boolean hasConnection(ChatConnection connection){
		return userList.containsKey(connection);
	}
	
	//Gets the user list from the Twitch API
	private static void fetchData(ChatConnection connection, ConcurrentHashMap<String, User> data) {
		try {
			// TWITCH API
			JSONObject object = URLLoader.getJSONFromURL(Globals.CONNECTED_USERS(connection.getChannelInfo().getChannel()));
			JSONObject chatters = (JSONObject) object.get("chatters");
			
			//Organizes the users by user type
			for (String user : ((List<String>) chatters.get("moderators")))
				addUser(connection, data, user, GlobalBadges.MODERATOR);
			
			for (String user : ((List<String>) chatters.get("staff")))
				addUser(connection, data, user, GlobalBadges.STAFF);
			
			for (String user : ((List<String>) chatters.get("admins")))
				addUser(connection, data, user, GlobalBadges.ADMIN);
			
			for (String user : ((List<String>) chatters.get("global_mods")))
				addUser(connection, data, user, GlobalBadges.GLOBAL_MOD);
			
			for (String user : ((List<String>) chatters.get("viewers")))
				addUser(connection, data, user);
			
			//Clears memory
			object.clear();
			chatters.clear();
		} catch (Exception e1) {
			data = null;
		}
	}
	
	//Adds new user to the user list
	private static User addUser(ChatConnection connection, ConcurrentHashMap<String, User> data, String user){
		user = user.toLowerCase();
		if(!data.containsKey(user))
			data.put(user, new User(connection, user));
		return data.get(user);
	}
	
	private static User addUser(ChatConnection connection, ConcurrentHashMap<String, User> data, String user, GlobalBadges type){
		addUser(connection, data, user);
		data.get(user).addType(connection.getBadge(type));
		return data.get(user);
	}
	
	public static User addUser(ChatConnection connection, String user){
		return addUser(connection, userList.get(connection), user);
	}
	
	public static User addUser(ChatConnection connection, String user, GlobalBadges type){
		return addUser(connection, userList.get(connection), user, type);
	}
	
	//Checks if the user exists
	public static boolean hasUser(ChatConnection connection, User user){
		return hasUser(connection, user.getUsername());
	}
	
	public static boolean hasUser(ChatConnection connection, String username){
		return userList.get(connection).containsKey(username.toLowerCase());
	}
	
	//Gets the user from user list
	public static User getUser(ChatConnection connection, String username){
		if(!hasUser(connection, username))
			return null;
		
		return userList.get(connection).get(username);
	}
	
	public static ConcurrentHashMap<String, User> getUserData(ChatConnection connection){
		return userList.get(connection);
	}
	
	public static synchronized int getUserCount(ChatConnection connection){
		int count = 0;
		for(String user : userList.get(connection).keySet())
			if(userList.get(connection).get(user).isConnected())
				count++;
		return count;
	}
	
	public static Object getLock(ChatConnection connection){
		return lockList.get(connection);
	}
}
