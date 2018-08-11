package twitch;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;

import twitch.connection.ChatConnection;
import twitch.files.Config;
import twitch.files.labels.LabelManager;
import twitch.gui.AttributeList;
import twitch.gui.Globals;
import twitch.gui.splash.StartupScreen;
import twitch.gui.splash.StartupScreenValues;
import twitch.gui.windows.TokenRequestWindow;
import twitch.gui.windows.Window;
import twitch.listeners.CustomListeners;
import twitch.listeners.InternalListeners;
import twitch.server.ServerList;
import twitch.user.badge.BitsImages;
import twitch.utils.Fonts;
import twitch.utils.Formats;
import twitch.utils.KeyPressed;

/*
 * Main method class
 */
public class Main {
	
	//Opens token request window. Closes program if invalid token retrieval 
	private static void getToken() {
		if (!(Globals.USERNAME == null || Globals.USERNAME.length() == 0 || Globals.TOKEN == null
				|| Globals.TOKEN.length() == 0))
			return;
		new TokenRequestWindow();
		//Failed to get token
		if (Globals.USERNAME == null || Globals.USERNAME.length() == 0 || Globals.TOKEN == null
				|| Globals.TOKEN.length() == 0)
			System.exit(0);
	}
	
	/*
	 * 
	 * MAIN METHOD
	 * 
	 */
	public static void main(String[] args) {
		//Starts up logging
		Log.setLogger(new MainLogger());
		
		//Loads the splash screen
		new StartupScreen();
		
		Log.info("Launching...");
		
		//Loads Fonts
		StartupScreen.updateProgressValue(StartupScreenValues.STARTUP);
		new Fonts();
		
		//Loads config files and tokens
		StartupScreen.updateProgressValue(StartupScreenValues.FILE_MIN);
		new Config();
		getToken();
		
		//Initiates chat connections and loads labels into memory
		StartupScreen.updateProgressValue(StartupScreenValues.FILE_MID);
		new ChatConnection();
		new LabelManager();
		
		//Creates default attributes
		StartupScreen.updateProgressValue(StartupScreenValues.FILE_MAX);
		new AttributeList();
		
		//Instantiates key listener
		StartupScreen.updateProgressValue(StartupScreenValues.STARTUP_INIT_ONE);
		new KeyPressed();
		
		//Loads listeners
		StartupScreen.updateProgressValue(StartupScreenValues.STARTUP_INIT_TWO);
		new CustomListeners();
		new InternalListeners();
		
		//Loads bits images and starts servers
		StartupScreen.updateProgressValue(StartupScreenValues.STARTUP_INIT_THREE);
		new BitsImages();
		new ServerList();
		//new FollowedChannels();
		
		//Launches the main GUI
		Window.launchWindow();
		
		//Closes startup screen
		StartupScreen.stop();
	}
	
	/*
	 * Main logger class
	 */
	static private class MainLogger extends Logger {
		//Logs new message
    	public void log (int level, String category, String message, Throwable ex) {
    		StringBuilder printBuilder = new StringBuilder(256);
    		StringBuilder errorBuilder = new StringBuilder(256);
    		printBuilder.append(Formats.getCurrentTimeStamp(System.currentTimeMillis()));
    		printBuilder.append(' ');
    		printBuilder.append(message);
    		if (ex != null) {
    			StringWriter writer = new StringWriter(256);
    			ex.printStackTrace(new PrintWriter(writer));
    			errorBuilder.append(writer.toString().trim());
    		}
    		String printOutput = printBuilder.toString();
    		System.out.println(printOutput);
    		StartupScreen.newProgressText(message);
    	}
    }
}
