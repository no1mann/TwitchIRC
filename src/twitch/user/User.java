package twitch.user;

import java.awt.Color;
import java.util.Collections;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

import twitch.channels.ChannelInfo;
import twitch.connection.ChatConnection;
import twitch.connection.URLLoader;
import twitch.gui.Globals;
import twitch.gui.MutableAttributeSet;
import twitch.message.types.Message;
import twitch.user.badge.BadgeType;
import twitch.user.badge.GlobalBadges;
import twitch.utils.ArrayUtils;
import twitch.utils.ChatStack;
import twitch.utils.RandomColor;

/*
 * User class for each instance of a user
 * Stores user customization options, badges, chat history, and channel info
 */
public class User implements Comparable<User>{

	//Unique Twitch ID
	private long userId = Long.MAX_VALUE;
	
	//Unique username
	private String username;
	
	//Display name based on username (caps)
	private String displayName;
	
	//Color of username
	private Color displayColor = RandomColor.getRandomColor();
	
	//List of badges
	private SortedSet<BadgeType> types;
	
	//History of chat messages
	private ChatStack<Message> chatHistory;
	
	//Total number of messages sent in the session
	private int totalMessageCount;
	
	//Chat connection instance the user belongs too
	private ChatConnection connection;
	
	//Channel info for the user
	private ChannelInfo info;
	
	
	private boolean checkedCustomBadges = false, isConnected = true;
	private MutableAttributeSet usernameAtt = null;
	
	public User(ChatConnection connection, String username){
		this.username = username;
		this.displayName = username;
		this.connection = connection;
		initialize();
	}
	
	public User(ChatConnection connection, String username, int amount){
		this(connection, username);
		this.setMessagesToLog(amount);
	}
	
	private void initialize(){
		types = Collections.synchronizedSortedSet(new TreeSet<BadgeType>());
		chatHistory = new ChatStack<Message>();
	}
	
	/*
	 * CHANNEL INFO
	 */
	public ChannelInfo getChannelInfo(){
		if(info == null)
			info = new ChannelInfo(this);
		return info;
	}
	
	/*
	 * USER TYPE METHODS
	 */
	public void addType(BadgeType type){
		if(isType(type))
			return;
		types.add(type);
	}
	
	public boolean isType(BadgeType type){
		if(type == null)
			return false;
		return types.contains(type);
	}
	
	public boolean isType(GlobalBadges type){
		return isType(getGlobalBadge(type));
	}
	
	private BadgeType getGlobalBadge(GlobalBadges badge){
		return connection.getBadge(badge);
	}
	
	public boolean hasType(){
		return types.size()!=0;
	}
	
	public boolean isBroadcaster(){
		return isType(GlobalBadges.BROADCASTER);
	}
	
	public boolean isMod(){
		return isType(GlobalBadges.MODERATOR) || 
				isType(GlobalBadges.BROADCASTER) || 
				isType(GlobalBadges.ADMIN) || 
				isType(GlobalBadges.GLOBAL_MOD) || 
				isType(GlobalBadges.STAFF);
	}
	
	public boolean isSubscriber(){
		return isType(GlobalBadges.SUBSCRIBER_0) ||
				isType(GlobalBadges.SUBSCRIBER_1) ||
				isType(GlobalBadges.SUBSCRIBER_3) ||
				isType(GlobalBadges.SUBSCRIBER_6) ||
				isType(GlobalBadges.SUBSCRIBER_12) ||
				isType(GlobalBadges.SUBSCRIBER_24);
	}
	
	public GlobalBadges getHighestBadge(){
		for(GlobalBadges badge : GlobalBadges.values())
			if(this.isType(badge))
				return badge;
		return null;
	}
	
	public void setTypes(TreeSet<BadgeType> types){
		this.types = Collections.synchronizedSortedSet(types);
	}
	
	public SortedSet<BadgeType> getTypes(){
		return types;
	}
	
	/*
	 * MESSAGES
	 */
	public Message onMessage(Message message){
		chatHistory.add(message);
		totalMessageCount++;
		return message;
	}
	
	public void setMessagesToLog(int amount){
		chatHistory.setMessagesToLog(amount);
	}
	
	public LinkedList<Message> getChatHistory(){
		return chatHistory;
	}

	public int getTotalMessageCount(){
		return totalMessageCount;
	}
	
	/*
	 * USER ID
	 */
	public void setUserID(long id){
		this.userId = id;
	}
	
	public long getUserID(){
		return userId;
	}
	
	/*
	 * USERNAMES
	 */
	public String getUsername(){
		return username;
	}
	
	public void setDisplayName(String name){
		this.displayName = name;
	}
	
	public String getDisplayName(){
		return this.displayName;
	}
	
	public String getBadgeName(){
		if(types.size() == 0)
			return getDisplayName();
		return getBadges() + getDisplayName();
	}
	
	public String getBadges(){
		String badges = "";
		for(BadgeType type : types){
			GlobalBadges badge = type.getBadge();
			if(badge == null)
				badge = GlobalBadges.OTHER;
			badges+="[" + badge.getShortName() + "] ";
		}
		return badges;
	}
	
	public String getLongBadges(){
		String badges = "";
		for(BadgeType type : types){
			GlobalBadges badge = type.getBadge();
			if(badge == null)
				badge = GlobalBadges.OTHER;
			badges+="[" + badge.getLongName() + "] ";
		}
		return badges;
	}
	
	public void setUsernameAttribute(MutableAttributeSet att){
		this.usernameAtt = att;
	}
	
	public boolean hasUsernameAttribute(){
		return this.usernameAtt != null;
	}
	
	public MutableAttributeSet getUsernameAttribute(){
		return this.usernameAtt;
	}
	
	/*
	 * COLORS
	 */
	public void setDisplayColor(Color color){
		this.displayColor = color;
		if(usernameAtt != null)
			usernameAtt.setColor(color);
	}
	
	public void setDisplayColor(String hex){
		if(hex==null || hex.equals(""))
			return;
		this.setDisplayColor(Color.decode(hex));
	}
	
	public Color getDisplayColor(){
		return displayColor;
	}
	
	/*
	 * OTHER DATA
	 */
	public String toString(){
		return getBadgeName();
	}
	
	public int hashCode(){
		return username.hashCode();
	}
	
	public boolean hasCheckedCustomBadges(){
		return checkedCustomBadges;
	}
	
	public void checkCustomBadges() {
		if (!checkedCustomBadges) {
			//CLIENT BADGES
			if (ArrayUtils.contains(UserList.ADMINS, username)) {
				//addType(connection.getBadge(BadgeManager.APP_ADMIN_NAME, BadgeManager.APP_ADMIN_VERSION));
				addType(getGlobalBadge(GlobalBadges.APP_DEV));
			}
			
			//FFZ
			try {
				String ffz = URLLoader.getSiteSourceCode(Globals.FFZ_DATA(username));
				if(ffz==null || ffz.equals(""))
					throw new Exception();
				if (ffz.contains("\"is_donor\":true"))
					addType(getGlobalBadge(GlobalBadges.FFZ));
			} catch (Exception e) {}
			checkedCustomBadges = true;
		}
	}
	
	public boolean equals(User user){
		if(user == null)
			return true;
		return user.getUsername().equals(this.getUsername());
	}

	@Override
	public int compareTo(User o) {
		BadgeType oType = null;
		if(connection.getBadgeList() == null)
			return 0;
		for(BadgeType type : connection.getBadgeList()){
			if(o.isType(type)){
				oType = type;
				break;
			}
		}
		BadgeType thisType = null;
		for(BadgeType type : connection.getBadgeList()){
			if(this.isType(type)){
				thisType = type;
				break;
			}
		}
		
		if(oType == thisType)
			return this.getUsername().toLowerCase().compareTo(o.getUsername().toLowerCase());
		else if(oType == null)
			return -1;
		else if(thisType == null)
			return 1;
		else
			return thisType.compareTo(oType);
	}
	
	public void setConnected(boolean val){
		this.isConnected = val;
	}
	
	public boolean isConnected(){
		return this.isConnected;
	}
}
