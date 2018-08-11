package twitch.files;

import java.awt.Color;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import com.esotericsoftware.minlog.Log;

import twitch.gui.Globals;
import twitch.server.ServerGlobals;

public class Config {
	
	private static final String loc = Globals.FILES_LOCATION + "\\config\\";

	private static ConcurrentHashMap<String, FileManager> fileList = new ConcurrentHashMap<String, FileManager>();
	
	//LOADS ALL CONFIGURATIONS FROM FILES
	static{
		try {	
			Database data;
			FileManager file;
			
			if(!new File(loc).exists())
				//File folder = new File(ClassLoader.getSystemResource("/res/splash.gif"));
			
			Log.info("Loading config files...");
			//CONNECTED CHANNELS
			file = new FileManager(loc + ConfigName.CHANNELS.getFileName());
			fileList.put(ConfigName.CHANNELS.getFileName(), file);
			file = new FileManager(loc + ConfigName.AUTOCONNECT.getFileName());
			fileList.put(ConfigName.AUTOCONNECT.getFileName(), file);
			file = new FileManager(loc + ConfigName.SELECTED.getFileName());
			fileList.put(ConfigName.SELECTED.getFileName(), file);
			
			//LOGIN DATA
			data = new Database(loc + ConfigName.LOGIN.getFileName());
			fileList.put(ConfigName.LOGIN.getFileName(), data);
			Globals.USERNAME = data.get("username");
			Globals.TOKEN = data.get("token");
			
			// SERVER DATA
			data = new Database(loc + ConfigName.SERVER.getFileName());
			fileList.put(ConfigName.SERVER.getFileName(), data);
			Globals.IRC_DOMAIN = data.get("IRC_DOMAIN");
			Globals.IRC_PORT = Integer.parseInt(data.get("IRC_PORT"));
			Globals.REDIRECT_URI_PORT = Integer.parseInt(data.get("REDIRECT_URI_PORT"));
			ServerGlobals.EMOTE_VIEW_PORT = Integer.parseInt(data.get("EMOTE_VIEW_PORT"));
			ServerGlobals.ALERT_BOX_PORT = Integer.parseInt(data.get("ALERT_BOX_PORT"));

			// ALERTS DATA
			data = new Database(loc + ConfigName.ALERTS.getFileName());
			fileList.put(ConfigName.ALERTS.getFileName(), data);
			AlertGlobals.ENABLE_ALERTS = Boolean.parseBoolean(data.get("ENABLE_ALERTS"));
			AlertGlobals.ALERTS_WIDTH = Integer.parseInt(data.get("ALERTS_WIDTH"));
			AlertGlobals.ALERTS_HEIGHT = Integer.parseInt(data.get("ALERTS_HEIGHT"));

			AlertGlobals.FOLLOW_ENABLED = Boolean.parseBoolean(data.get("FOLLOW_ENABLED"));
			AlertGlobals.FOLLOW_IMAGE = data.get("FOLLOW_IMAGE");
			AlertGlobals.FOLLOW_AUDIO = data.get("FOLLOW_AUDIO");
			AlertGlobals.FOLLOW_TEXT = data.get("FOLLOW_TEXT");
			AlertGlobals.FOLLOW_USER_COLOR = Color.decode(data.get("FOLLOW_USER_COLOR"));
			AlertGlobals.FOLLOW_TEXT_COLOR = Color.decode(data.get("FOLLOW_TEXT_COLOR"));
			AlertGlobals.FOLLOW_ALERT_LENGTH = Integer.parseInt(data.get("FOLLOW_ALERT_LENGTH"));

			AlertGlobals.SUB_ENABLED = Boolean.parseBoolean(data.get("SUB_ENABLED"));
			AlertGlobals.SUB_IMAGE = data.get("SUB_IMAGE");
			AlertGlobals.SUB_AUDIO = data.get("SUB_AUDIO");
			AlertGlobals.SUB_TEXT = data.get("SUB_TEXT");
			AlertGlobals.RESUB_TEXT = data.get("RESUB_TEXT");
			AlertGlobals.RESUB_TTS = Boolean.parseBoolean(data.get("RESUB_TTS"));
			AlertGlobals.SUB_USER_COLOR = Color.decode(data.get("SUB_USER_COLOR"));
			AlertGlobals.SUB_LENGTH_COLOR = Color.decode(data.get("SUB_LENGTH_COLOR"));
			AlertGlobals.SUB_TEXT_COLOR = Color.decode(data.get("SUB_TEXT_COLOR"));
			AlertGlobals.SUB_ALERT_LENGTH = Integer.parseInt(data.get("SUB_ALERT_LENGTH"));
			
			AlertGlobals.HOST_ENABLED = Boolean.parseBoolean(data.get("HOST_ENABLED"));
			AlertGlobals.HOST_IMAGE = data.get("HOST_IMAGE");
			AlertGlobals.HOST_AUDIO = data.get("HOST_AUDIO");
			AlertGlobals.HOST_TEXT = data.get("HOST_TEXT");
			AlertGlobals.HOST_USER_COLOR = Color.decode(data.get("HOST_USER_COLOR"));
			AlertGlobals.HOST_AMOUNT_COLOR = Color.decode(data.get("HOST_AMOUNT_COLOR"));
			AlertGlobals.HOST_TEXT_COLOR = Color.decode(data.get("HOST_TEXT_COLOR"));
			AlertGlobals.HOST_MINIMUM = Integer.parseInt(data.get("HOST_MINIMUM"));
			AlertGlobals.HOST_ALERT_LENGTH = Integer.parseInt(data.get("HOST_ALERT_LENGTH"));
			
			AlertGlobals.BITS_ENABLED = Boolean.parseBoolean(data.get("BITS_ENABLED"));
			AlertGlobals.BITS_TEXT = data.get("BITS_TEXT");
			AlertGlobals.BITS_TTS = Boolean.parseBoolean(data.get("BITS_TTS"));
			AlertGlobals.BITS_USER_COLOR = Color.decode(data.get("BITS_USER_COLOR"));
			AlertGlobals.BITS_AMOUNT_COLOR = Color.decode(data.get("BITS_AMOUNT_COLOR"));
			AlertGlobals.BITS_TEXT_COLOR = Color.decode(data.get("BITS_TEXT_COLOR"));
			AlertGlobals.BITS_MINIMUM = Integer.parseInt(data.get("BITS_MINIMUM"));
			AlertGlobals.BITS_ALERT_LENGTH = Integer.parseInt(data.get("BITS_ALERT_LENGTH"));
		} catch (Exception e) {
			Log.info("Error loading config files\n\n" + e);
		}
	}
	
	//Save all configurations
	public static void saveAll(){
		for(FileManager file : fileList.values())
			file.save();
	}
	
	public static FileManager getConfig(ConfigName name){
		return fileList.get(name.getFileName());
	}
	
	public static String getData(ConfigName name, String key){
		if(getConfig(name) instanceof Database)
			return ((Database)getConfig(name)).get(key);
		return null;
	}
}
