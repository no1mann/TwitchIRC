package twitch.listeners;

import twitch.gui.Globals;
import twitch.listeners.types.ListenerType;
import twitch.message.types.ChatMessage;

/*
 * Internal listeners allows custom listeners to be called internally in the program.
 */
public class InternalListeners {
	
	//List of admins who can control the client remotely through Twitch chat
	public static String[] admins = {
		Globals.USERNAME
	};
	
	//Checks message for command and triggers command event
	public static void checkCommand(ChatMessage message){
		//If message is a command the user is admin
		if(message.getMessage().startsWith("~") && contains(message.getSender().getUsername()))
			GlobalListeners.triggerListener(ListenerType.ON_COMMAND, new CommandEvent(message.getSender(), message.getMessage().substring(1)));
	}
	
	public static boolean contains(String val){
		for(String s : admins)
			if(val.equalsIgnoreCase(s))
				return true;
		return false;
	}
}
