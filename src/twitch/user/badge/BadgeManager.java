package twitch.user.badge;

import java.util.Collection;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;

import com.esotericsoftware.minlog.Log;

import twitch.channels.ChannelInfo;
import twitch.connection.URLLoader;
import twitch.gui.Globals;
import twitch.utils.Dialogs;

/*
 * Manages all badges by loading them into memory
 */
public class BadgeManager {

	public static final String APP_ADMIN_NAME = "app_dev";
	public static final String APP_ADMIN_VERSION = "1";
	
	//Stores list of badges for references
	private ConcurrentHashMap<String, BadgeType> badgeList = null;
	//Stores badges in sorted order
	private TreeSet<BadgeType> values = null;
	
	public BadgeManager(){
		Log.debug("Badge Manager instantiated...");
		this.badgeList = new ConcurrentHashMap<String, BadgeType>();
		this.values = new TreeSet<BadgeType>();
		loadGlobalBadges();
	}
	
	
	/*
	 * Parses badges from Twitch API
	 */
	private static JSONObject getVersionList(JSONObject obj, String key){
		return (JSONObject)((JSONObject)(obj).get(key)).get("versions");
	}
	
	private void loadBadgeType(JSONObject obj, String channel){
		JSONObject versionObj = null, badgeVersionObj = null;
		for (Object badgeNameO : obj.keySet()) {
			String badgeName = (String) badgeNameO;
			versionObj = getVersionList(obj, badgeName);
			for (Object versionO : versionObj.keySet()) {
				String version = (String) versionO;
				badgeVersionObj = (JSONObject) (versionObj).get(version);
				if(channel == null)
					addBadge(badgeName, version, badgeVersionObj);
				else
					addBadge(badgeName, version, badgeVersionObj, channel);
				
			}
		}
	}
	
	/*
	 * Adds badges based on the Twitch API
	 */
	private void addBadge(String name, String version, JSONObject badgeVersionObj){
		newBadge(name, version, (String) badgeVersionObj.get("title"), (String) badgeVersionObj.get("description"), (String) badgeVersionObj.get("image_url_1x"), true);
	}
	
	private void addBadge(String name, String version, JSONObject badgeVersionObj, String channel){
		newBadge(name, version, (String) badgeVersionObj.get("title"), (String) badgeVersionObj.get("description"), (String) badgeVersionObj.get("image_url_1x"), true, channel);
	}
	
	private void newBadge(String name, String version, String title, String description, String image, boolean isURL){
		BadgeType type = new BadgeType(name, version, title, description, image, isURL);
		badgeList.put(name + version, type);
	}
	
	private void newBadge(String name, String version, String title, String description, String image, boolean isURL, String channel){
		BadgeType type = new BadgeType(name, version, title, description, image, isURL);
		badgeList.put(channel + name + version, type);
	}
	
	//Loads all global badges from Twitch and FFZ
	private void loadGlobalBadges() {
		Log.debug("Loading global badges...");
		JSONObject obj = null;
		try {
			//GLOBAL BADGES
			obj = ((JSONObject) URLLoader.getJSONFromURL(Globals.BADGE_URL).get("badge_sets"));
			loadBadgeType(obj, null);
			//OTHER BADGES
			newBadge("ffz", "1", "FrankerFaceZ", "FrankerFaceZ donator", Globals.FFZ_ICON, true);
			badgeList.get("ffz1").getAttribute().setBackground(GlobalBadges.FFZ.getColor());
			newBadge(APP_ADMIN_NAME, APP_ADMIN_VERSION, "App Developer", "Developer of client", Globals.ADMIN_BADGE, false);
			
			for(BadgeType type: badgeList.values())
				values.add(type);
			
		} catch (Exception e) {
			Log.error("Error loading global badges", e);
			Dialogs.error("Error", "Error loading badges:\nDisabling all images.");
			e.printStackTrace();
		}
	}
	
	//Loads badges for a specific channel
	public void loadChannelBadges(ChannelInfo info){
		this.loadChannelBadges(info, 0);
	}
	
	//Loads badges for a specific channel with attempts
	private void loadChannelBadges(ChannelInfo info, int amount){
		Log.debug("Loading channel badges for " + info.getDisplayName() + "...");
		JSONObject obj = null;
		try {
			//CHANNEL BADGES
			if(info.isPartnered()){
				//Load custom sub badges
				obj = ((JSONObject) URLLoader.getJSONFromURL(Globals.CHANNEL_BADGE_URL(info.getId())).get("badge_sets"));
				loadBadgeType(obj, info.getChannel());	
			}
			for(BadgeType type: badgeList.values())
				values.add(type);
		} catch (Exception e) {
			if(amount != 3)
				this.loadChannelBadges(info, amount+1);
			else{
				Log.error("Error loading badges for " + info.getDisplayName(), e);
				Dialogs.error("Error", "Error loading badges:\nDisabling all images.");
				e.printStackTrace();
			}
		}
	}
	
	//If badges loaded successfully
	public boolean loadedSuccesfully(){
		return badgeList!=null;
	}
	
	//Checks if a specific badge has loaded
	public boolean hasBadge(String channel, String name, String version){
		if(badgeList == null)
			return false;
		BadgeType type = badgeList.get(channel + name + version);
		if(type == null)
			return hasBadge(name, version);
		return badgeList.containsKey(channel + name + version);
	}
	
	private boolean hasBadge(String name, String version){
		return badgeList.containsKey(name + version);
	}
	
	//Gets the badge from a specific channel
	public BadgeType getBadge(String channelName, String name, String version){
		if(badgeList == null)
			return null;
		BadgeType type = badgeList.get(channelName + name + version);
		if(type == null)
			return getBadge(name, version);
		return type;
	}
	
	private BadgeType getBadge(String name, String version){
		if(badgeList == null)
			return null;
		return badgeList.get(name + version);
	}
	
	public BadgeType getBadge(String channelName, GlobalBadges badge){
		if(badgeList == null)
			return null;
		if(badge.toString().contains("BITS"))
			return getBadge(channelName, "bits", badge.toString().substring(badge.toString().indexOf("_")+1));
		else if(badge.toString().contains("SUBSCRIBER"))
			return getBadge(channelName, "subscriber", badge.toString().substring(badge.toString().indexOf("_")+1));
		else
			return getBadge(badge);
	}
	
	private BadgeType getBadge(GlobalBadges badge){
		if(badgeList == null)
			return null;
		for(BadgeType badgeType : badgeList.values())
			if(badgeType.getBadge() == badge)
				return badgeType;
		return null;
	}
	
	public Collection<BadgeType> getBadgeList(){
		return values;
	}
}
