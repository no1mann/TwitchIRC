package twitch.channels;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

import org.json.simple.JSONObject;

import twitch.connection.URLLoader;
import twitch.gui.Globals;
import twitch.user.User;
import twitch.utils.Dialogs;

public class ChannelInfo {

	private static BufferedImage defaultLogo;
	
	//Channel data
	private String game, created, language, displayName, url, updatedAt, name, broadcasterLanguage, status;
	private boolean mature, partner;
	private BufferedImage logo;
	private long views, id, followers;
	private Color profileColor;
	
	private boolean success = false;
	private String channel;
	
	
	//Load default channel info
	static{
		try {
			//Default profile picture
			defaultLogo = parseImage(Globals.DEFAULT_PROFILE_PIC);
		} catch (Exception e) {}
	}
	
	public ChannelInfo(User user){
		this(user.getUsername());
	}
	
	//Main constructor
	public ChannelInfo(String channel){
		setChannel(channel);
		try {
			//Loads JSOBObject from Twitch API
			JSONObject data = URLLoader.getKrakenAPI(Globals.CHANNEL_DATA(channel), false, true);
			this.parseData(data);
			data = null;
		} catch (Exception e) {
			Dialogs.error("Error", "Error loading kraken API\n" + Globals.CHANNEL_DATA(channel));
			return;
		}
		success = true;
	}
	
	public ChannelInfo(){
	}
	
	//Sets channel name - Only use before channel parsing using parseData method
	public void setChannel(String channel){
		if(channel.startsWith("#")){
			this.channel = channel.substring(1);
		}
		else{
			this.channel = channel;
		}
	}
	
	//Parses JSONObject from Twitch API
	public void parseData(JSONObject data){
		
		//GAME BEING PLAYED
		try{
			game = (String)data.get("game");
		} catch(Exception e){
			game = "";
		}

		//MATURE AUDIENCES
		try{
			mature = (boolean) data.get("mature");
		} catch(Exception e){
			mature = false;
		}
		
		//CHANNEL CREATION DATE
		try{
			created = (String)data.get("created_at");
		} catch(Exception e){
			created = "";
		}
		
		//CHANNEL LANGUAGE
		try{
			language = (String)data.get("language");
		} catch(Exception e){
			language = "unknown";
		}
		
		//PROFILE BANNER COLOR
		try{
			profileColor = parseColor((String)data.get("profile_banner_background_color"));
		} catch(Exception e){
			profileColor = Color.WHITE;
		}
		
		//DISPLAY NAME
		try{
			displayName = (String)data.get("display_name");
		} catch(Exception e){
			displayName = channel;
		}
		
		//CHANNEL URL
		try{
			url = (String)data.get("url");
		} catch(Exception e){
			url = "https://twitch.tv/" + channel;
		}
		
		//NUMBER OF FOLLOWERS
		try{
			followers = (long)data.get("followers");
		} catch(Exception e){
			followers = 0;
		}
		
		//CHANNEL UPDATED TIME
		try{
			updatedAt = (String)data.get("updated_at");
		} catch(Exception e){
			updatedAt = "";
		}
		
		//IS THE CHANNEL PARTNER
		try{
			partner = (boolean) data.get("partner");
		} catch(Exception e){
			partner = false;
		}
		
		//CHANNEL LANGUAGE
		try{
			broadcasterLanguage = (String)data.get("broadcaster_language");
		} catch(Exception e){
			broadcasterLanguage = language;
		}
		
		//CHANNEL USERNAME
		try{
			name = (String)data.get("name");
		} catch(Exception e){
			name = channel;
		}
		
		//CHANNEL PROFILE PICTURE
		try {
			logo = parseImage((String)data.get("logo"));
		} catch (Exception e) {
			logo = defaultLogo;
		}
		
		//CHANNEL ID NUMBER
		try{
			id = (long)data.get("_id");
		} catch(Exception e){
			id = 0;
		}
		
		//NUMBER OF VIEWS
		try{
			views = (long)data.get("views");
		} catch(Exception e){
			views = 0;
		}
		
		//CHANNEL TITLE
		try{
			status = (String)data.get("status");
		} catch(Exception e){
			status = "";
		}
		
		//Clear JSONObject for memory reasons
		data.clear();
	}
	
	//Loads Image from URL
	private static BufferedImage parseImage(String value) throws Exception{
			return ImageIO.read(new URL(value));
	}
	
	//Loads color from HEX val
	private static Color parseColor(String value){
		if(value==null || value.equals("null")){
			return null;
		}
		return Color.decode(value);
	}

	public synchronized String getGame() {
		return game;
	}

	public synchronized String getCreated() {
		return created;
	}

	public synchronized String getLanguage() {
		return language;
	}

	public synchronized String getDisplayName() {
		return displayName;
	}

	public synchronized String getUrl() {
		return url;
	}

	public synchronized String getUpdatedAt() {
		return updatedAt;
	}

	public synchronized String getName() {
		return name;
	}

	public synchronized String getBroadcasterLanguage() {
		return broadcasterLanguage;
	}

	public synchronized String getStatus() {
		return status;
	}

	public synchronized boolean isMature() {
		return mature;
	}

	public synchronized boolean isPartnered() {
		return partner;
	}

	public synchronized BufferedImage getLogo() {
		return logo;
	}

	public synchronized long getViews() {
		return views;
	}

	public synchronized long getId() {
		return id;
	}

	public synchronized long getFollowers() {
		return followers;
	}

	public synchronized Color getProfileColor() {
		return profileColor;
	}

	public synchronized String getChannel() {
		return channel;
	}
	
	public synchronized boolean loadedSuccessfully(){
		return success;
	}
	
}
