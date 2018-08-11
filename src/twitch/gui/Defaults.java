package twitch.gui;

import twitch.connection.ConnectionList;
import twitch.files.Config;
import twitch.files.ConfigName;
import twitch.files.FileManager;
import twitch.gui.splash.StartupScreen;
import twitch.gui.splash.StartupScreenValues;
import twitch.gui.windows.Window;

/*
 * Default config information
 */
public class Defaults {
	
	//Loads all the channels and connects too them, updates status bars and  
	public static void loadDefaults(){
		FileManager manager = Config.getConfig(ConfigName.CHANNELS);
		StartupScreen.updateProgressValue(StartupScreenValues.CONNECTION_MIN);
		int counter = 0;
		//Cycles through list of channels from config file and connects too them
		for(String line : manager.getLines()){
			if(line == null || line.equals("") || line.equals(" "))
				continue;
			ConnectionList.setupNewConnection(line, false);
			double val = StartupScreenValues.CONNECTION_MIN.getValue() + (StartupScreenValues.CONNECTION_MAX.getValue() - StartupScreenValues.CONNECTION_MIN.getValue()) * ((double)counter/manager.getLineCount());
			StartupScreen.updateProgressValue(val);
			counter++;
		}
		manager = Config.getConfig(ConfigName.AUTOCONNECT);
		counter = 0;
		//Cycles through list of streams too join from config file and joins them
		for(String line : manager.getLines()){
			if(line == null || line.equals("") || line.equals(" "))
				continue;
			ConnectionList.setupNewConnection(line, true);
			double val = StartupScreenValues.CONNECT_MIN.getValue() + (StartupScreenValues.CONNECT_MAX.getValue() - StartupScreenValues.CONNECT_MIN.getValue()) * ((double)counter/manager.getLineCount());
			StartupScreen.updateProgressValue(val);
			counter++;
		}
		//Gets the selected stream and selects it
		manager = Config.getConfig(ConfigName.SELECTED);
		if(manager.getLine(0) != null && manager.getLine(0).length() > 0)
			Window.changeChannels(manager.getLine(0));
		StartupScreen.updateProgressValue(StartupScreenValues.CONNECT_MAX);
	}
	
	//Saves the default channels, and streams
	public static void saveDefaults(){
		FileManager manager = Config.getConfig(ConfigName.CHANNELS);
		manager.clearLines();
		for(String line : ConnectionList.getChannelNames())
			manager.addLine(line);
		manager.save();
		manager = Config.getConfig(ConfigName.AUTOCONNECT);
		manager.clearLines();
		for(String line : ConnectionList.getChannelNames())
			if(ConnectionList.getChannel(line).isConnected())
				manager.addLine(line);
		manager.save();
		manager = Config.getConfig(ConfigName.SELECTED);
		manager.clearLines();
		if(Window.getCurrentConnection()!=null)
			manager.setLine(0, Window.getCurrentConnection().getChannelName());
		manager.save();
	}
	
}
