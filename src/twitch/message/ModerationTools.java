package twitch.message;

import java.awt.Color;

import javax.swing.JOptionPane;

import twitch.connection.ChatConnection;
import twitch.gui.Globals;
import twitch.gui.windows.Window;
import twitch.user.User;
import twitch.user.UserList;
import twitch.user.badge.GlobalBadges;
import twitch.utils.Dialogs;

public class ModerationTools {

	/*
	 * COMMANDS
	 */
	
	public static void commercial(int seconds){
		customCommand("/commercial " + seconds, true, false);
	}
	
	public static void host(String user){
		customCommand("/host " + user.toLowerCase(), true, false);
	}
	
	public static void unhost(){
		customCommand("/unhost", true, false);
	}
	
	public static void mods(){
		customCommand("/mods", false, false);
	}
	
	public static void color(Color color){
		customCommand("/color #" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase(), false, false);
	}
	
	public static void ignore(User user){
		customCommand("/ignore " + user.getUsername(), false, false);
	}
	
	public static void unignore(User user){
		customCommand("/unignore " + user.getUsername(), false, false);
	}
	
	public static void me(String message){
		customCommand("/me " + message, false, false);
	}
	
	public static void subMode(boolean enable){
		if(enable)
			customCommand("/subscribers", true, false);
		else
			customCommand("/subscribersoff", true, false);
	}
	
	public static void followerMode(boolean enable, int minutes){
		if(enable)
			customCommand("/followers " + minutes + "m", true, false);
		else
			customCommand("/followersoff", true, false);
	}
	
	public static void r9KMode(boolean enable){
		if(enable)
			customCommand("/r9kbeta", true, false);
		else
			customCommand("/r9kbetaoff", true, false);
	}
	
	public static void slowMode(boolean enable, int seconds){
		if(enable)
			customCommand("/slow " + seconds, true, false);
		else
			customCommand("/slowoff", true, false);
	}
	
	public static void emoteMode(boolean enable){
		if(enable)
			customCommand("/emoteonly", true, false);
		else
			customCommand("/emoteonlyoff", true, false);
	}
	
	public static void clear(){
		customCommand("/clear", true, false);
	}
	
	public static void mod(User user){
		customCommand("/mod " + user.getUsername(), false, true);
	}
	
	public static void unmod(User user){
		customCommand("/unmod " + user.getUsername(), false, true);
	}
	
	public static void unban(User user){
		customCommand("/unban " + user.getUsername(), true, false);
	}
	
	public static void ban(User user, String reason){
		customCommand("/ban " + user.getUsername() + " " + reason, true, false);
	}
	
	public static void ban(User user){
		ban(user, "");
	}
	
	public static void purge(User user){
		timeout(user, 1);
	}
	
	public static void timeout(User user, int amount){
		customCommand("/timeout " + user.getUsername() + " " + amount, true, false);
	}
	
	
	//Checks if the command is valid (moderator or broadcaster)
	public static void customCommand(String command, boolean requireMod, boolean requireBroadcaster){
		if(!Dialogs.checkConnected())
			return;
		//Is mod and mod is required
		if(requireMod && isMod(Window.getCurrentConnection()))
			Window.getCurrentConnection().sendMessage(command);
		//Is broadcast and requires broadcaster
		else if(requireBroadcaster && isBroadcaster(Window.getCurrentConnection()))
			Window.getCurrentConnection().sendMessage(command);
		//If requires mod and you're not mod, notify
		else if(requireMod)
			notModMessage();
		//If requires broadcaster and you're not broadcaster, notify
		else if(requireBroadcaster)
			notBroadcasterMessage();
		else
			Window.getCurrentConnection().sendMessage(command);
	}
	
	public static void notModMessage(){
		JOptionPane.showMessageDialog(null, "You must be a moderator of this channel.");
	}
	
	public static void notBroadcasterMessage(){
		JOptionPane.showMessageDialog(null, "You must be the broadcaster of this channel.");
	}
	
	public static boolean isBroadcaster(ChatConnection connection){
		User you = getThisAccount(connection);
		if(you == null)
			return Globals.USERNAME.equalsIgnoreCase(Window.getCurrentConnection().getChannelName().substring(1));
		return you.isType(GlobalBadges.BROADCASTER); 
	}
	
	public static boolean isMod(ChatConnection connection){
		User you = getThisAccount(connection);
		if(you == null)
			return true;
		return you.isMod();
	}
	
	public static boolean isNull(ChatConnection connection){
		return getThisAccount(connection) == null;
	}
	
	//Gets the logged in user account
	private static User getThisAccount(ChatConnection connection){
		return UserList.getUser(connection, Globals.USERNAME.toLowerCase());
	}
	
}
