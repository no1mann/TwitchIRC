package twitch.server;

import java.io.File;
import java.util.HashSet;

import com.esotericsoftware.minlog.Log;

import twitch.gui.splash.StartupScreen;
import twitch.gui.splash.StartupScreenValues;

/*
 * List of all servers for future overlays. Work in progress
 */
public class ServerList {

	private static HashSet<Server> serverList;
	
	static{
		Log.info("Initiallizing server list");
		serverList = new HashSet<Server>();
	}
	
	public static void loadAllServers(){
		Log.info("Starting servers...");
		StartupScreen.updateProgressValue(StartupScreenValues.SERVER_MIN);
		serverList.add(new Server(ServerGlobals.ALERT_BOX_PORT, new File(ServerGlobals.ALERT_BOX_LOCATION)));
		serverList.add(new Server(ServerGlobals.EMOTE_VIEW_PORT, new File(ServerGlobals.EMOTE_VIEW_LOCATION)));
		Log.info("All servers started...");
		StartupScreen.updateProgressValue(StartupScreenValues.SERVER_MAX);
	}
	
	public static synchronized void add(Server server){
		serverList.add(server);
	}
	
	public static synchronized void remove(Server server){
		serverList.remove(server);
	}
	
	public static synchronized boolean contains(Server server){
		return serverList.contains(server);
	}
	
	public static synchronized void startAll(){
		for(Server server : serverList)
			server.start();
	}
	
	public static synchronized void stopAll(){
		for(Server server : serverList)
			server.stop();
	}
	
	public static synchronized void clear(){
		serverList.clear();
	}
	
	public static synchronized HashSet<Server> getServers(){
		return serverList;
	}
}
