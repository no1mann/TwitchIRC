package twitch.gui;

import javax.swing.JFileChooser;

import twitch.connection.SearchType;

public class Globals {
	/*
	 * APPLICATION DATA
	 */
	public static final String NAME = "TwitchIRC";
	
	
	
	/*
	 * AUTHORIZATION DATA
	 */
	public static final String KRAKEN_URL = "https://api.twitch.tv/kraken/";
	public static final String CLIENT_ID = "ngeb0nzb67gtkkyw2znd909qn4eed8";
	public static int REDIRECT_URI_PORT = 61324;
	public static final String REDIRECT_URI = "http://127.0.0.1:" + REDIRECT_URI_PORT + "/";
	public static final String AUTORIZATION_URL = KRAKEN_URL + "oauth2/authorize";
	public static final String AUTORIZATION_PARAM = "?response_type=token"
			+ "&client_id=" + Globals.CLIENT_ID
			+ "&redirect_uri=" + Globals.REDIRECT_URI
			+ "&force_verify=true"
			+ "&scope=chat_login+user_read+user_subscriptions+channel_editor+channel_commercial+channel_subscriptions";
	public static String TOKEN;
	
	
	
	/*
	 * IRC DATA
	 */
	public static String IRC_DOMAIN = "irc.chat.twitch.tv";
	public static int IRC_PORT = 6667;

	
	
	/*
	 * ACCOUNT DATA
	 */
	public static String USERNAME;
	
	
	/*
	 * API URLS
	 */
	//TWITCH
	//Gets the global badges
	public static final String BADGE_URL = "https://badges.twitch.tv/v1/badges/global/display";
	//Gets the badges of a channel
	public static final String CHANNEL_BADGE_URL(long id){return "https://badges.twitch.tv/v1/badges/channels/" + id +"/display";}
	//Gets the list of users connected to a channel
	public static final String CONNECTED_USERS(String channel){return "http://tmi.twitch.tv/group/user/" + channel + "/chatters";}
	//Gets the followers a user has
	public static final String CHANNEL_FOLLOWERS(String channel, int amount){return KRAKEN_URL + "channels/" + channel + "/follows?limit=" + amount;}
	//Gets the emote based on an id
	public static final String EMOTE_IMAGE(String id){return "http://static-cdn.jtvnw.net//emoticons/v1/" + id + "/1.0";}
	//Gets channel data
	public static final String CHANNEL_DATA(String channel){return KRAKEN_URL + "channels/" + channel;}
	//Gets subscription data
	public static final String SUBSCRIPTION_DATA(String channel, int amount){return CHANNEL_DATA(channel) + "/subscriptions?limit=" + amount;}
	//Gets stream data
	public static final String STREAM_DATA(String channel){return KRAKEN_URL + "streams/" + channel;}
	//Gets the bits image
	public static final String BITS_IMAGE_URL(int bits){return "https://d3aqoihi2n8ty8.cloudfront.net/actions/cheer/dark/animated/" + bits + "/1.gif";}
	//Gets followed data
	public static final String FOLLOWED_CHANNELS = KRAKEN_URL + "streams/followed";
	//Search
	public static final String SEARCH(SearchType type, String query, int offset, int limit){return KRAKEN_URL + "search/" + type.name().toLowerCase() + "?query=" + query + "&type=suggest" + "&limit=" + limit + "&offset=" + offset;}
	//Gets stream thumbnail
	public static final String STREAM_THUMBNAIL(String channel, int width, int height){return "https://static-cdn.jtvnw.net/previews-ttv/live_user_" + channel + "-" + width + "x" + height + ".jpg";}
	
	//FFZ
	//Global FFZ data
	public static final String FFZ_GLOBALS = "https://api.frankerfacez.com/v1/set/global";
	//Channel FFZ Data
	public static final String FFZ_CHANNELS(String channel){return "https://api.frankerfacez.com/v1/room/" + channel;}
	//Emote FFZ Data
	public static final String FFZ_EMOTE(String id){return "https://cdn.frankerfacez.com/emoticon/" + id + "/1";}
	//Get FFZ data
	public static final String FFZ_DATA(String username){return "https://api.frankerfacez.com/v1/_user/" + username.toLowerCase();}
	
	//BTTV
	//Global BTTV data
	public static final String BTTV_GLOBALS = "https://api.betterttv.net/2/emotes";
	//Channel BTTV data
	public static final String BTTV_CHANNELS(String channel){return "https://api.betterttv.net/2/channels/" + channel;}
	//Gets BTTV emote
	public static final String BTTV_EMOTE(String id){return "https://cdn.betterttv.net/emote/" + id + "/1x";}
	
	/*
	 * FILES
	 */
	public static final String FILES_LOCATION = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\" + Globals.NAME;
	
	/*
	 * IMAGES
	 */
	public static final String IMAGE_LOCATION = "res/";
	public static final String APP_ICON = IMAGE_LOCATION + "icon.png/";
	public static final String ADMIN_BADGE = IMAGE_LOCATION + "admin_badge.png";
	public static final String BAN_ICON = IMAGE_LOCATION + "ban_icon.png";
	public static final String TIMEOUT_ICON = IMAGE_LOCATION + "timeout_icon.png";
	public static final String FFZ_ICON = IMAGE_LOCATION + "ffz_icon.png";
	public static final String LOADING_ICON = IMAGE_LOCATION + "loading.gif";
	public static final String DEFAULT_PROFILE_PIC = "https://static-cdn.jtvnw.net/jtv_user_pictures/xarth/404_user_600x600.png";
	
}
