package twitch.listeners.types;

import twitch.listeners.CommandEvent;

public interface CommandListener {

	public void onCommand(CommandEvent command);
	
}
