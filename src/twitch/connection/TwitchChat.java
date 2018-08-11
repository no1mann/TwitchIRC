package twitch.connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.jibble.pircbot.PircBot;

import twitch.listeners.InternalListeners;
import twitch.message.*;
import twitch.message.emotes.ChatEmote;
import twitch.message.emotes.Emote;
import twitch.message.emotes.EmoteManager;
import twitch.message.emotes.EmoteType;
import twitch.message.types.BanMessage;
import twitch.message.types.BitMessage;
import twitch.message.types.ChatMessage;
import twitch.message.types.ChatModeMessage;
import twitch.message.types.ClearChatMessage;
import twitch.message.types.Message;
import twitch.message.types.SubscriberMessage;
import twitch.user.User;
import twitch.user.UserList;
import twitch.user.badge.BadgeType;

public class TwitchChat extends PircBot{
	
	//Single instance to track and store all emotes
	public static EmoteManager emoteManager = new EmoteManager();
	
	/*
	 * Extracts data from the chat message JSONObject
	 * HashMap data stores the results of the message
	 */
	
	private void extractData(ChatConnection connection, HashMap<String, String> data, String message){
		String[] tags = message.split("tmi.twitch.tv ");
		for (String item : new ArrayList<String>(Arrays.asList(tags[0].split(";")))) {
			if(!item.contains("=")){
				data = null;
				return;
			}
			String key = item.substring(0, item.indexOf('='));
			String value = item.substring(item.indexOf('=') + 1);
			data.put(key, value);
		}
		data.put("tag", tags[1].substring(0, tags[1].indexOf(connection.getChannelName())-1));
	}
	
	private User getUser(ChatConnection connection, String username){
		UserList.addUser(connection, username.toLowerCase());
		return UserList.getUser(connection, username.toLowerCase());
	}
	
	//Removes escaped keys
	private String replaceSpaces(String message){
		return message.replaceAll(Pattern.quote("\\s"), " ");
	}
	
	private final String val = " #";
	private final int count = val.length();
	
	/*
	 * Called for every chat message received
	 * String mes is a JSONObejct with the chat message info
	 */
	public void onUnknown(String mes) {
		long time = System.currentTimeMillis();
		//Ignores configuration messages
		if (mes.equals(":tmi.twitch.tv CAP * ACK :twitch.tv/membership")
				|| mes.equals(":tmi.twitch.tv CAP * ACK :twitch.tv/tags")
				|| mes.equals(":tmi.twitch.tv CAP * ACK :twitch.tv/commands"))
			return;
		String tempChan = mes.substring(mes.indexOf(val) + count - 1);
		String channel = null;
		if(tempChan.indexOf(" :") == -1)
			channel = tempChan;
		else
			channel = tempChan.substring(0, tempChan.indexOf(" :"));
		ChatConnection connection = ConnectionList.getChannel(channel);
		final String tags = mes.substring(1, mes.indexOf(" " + connection.getChannelName()) + (" " + connection.getChannelName()).length());
		// Private Message, Will implement later!
		if (mes.indexOf(" " + connection.getChannelName()) == -1) 
			return;
		HashMap<String, String> data = new HashMap<String, String>();
		extractData(connection, data, tags);
		// ERROR PARSING MESSAGE
		if (data == null || !data.containsKey("tag")) 
			return;
		String tag = data.get("tag");
		String msgSplit = ":tmi.twitch.tv " + tag + " " + connection.getChannelName();
		User user = null;
		Message message = null;
		/*
		 * BAN TIMEOUT CLEAR CHAT
		 */
		if (tag.equalsIgnoreCase("CLEARCHAT")) {
			String username = null;
			if (mes.indexOf(msgSplit) + msgSplit.length() + 2 <= mes.length()) {
				username = mes.substring(mes.indexOf(msgSplit) + msgSplit.length() + 2);
				user = getUser(connection, username);
			}
			if (username != null && data.containsKey("ban-reason")) {
				if (data.containsKey("ban-duration") && data.get("ban-duration").length() != 0) 
					message = new BanMessage(connection, time, user, Integer.parseInt(data.get("ban-duration")));
				else 
					message = new BanMessage(connection, time, user);
			} else 
				message = new ClearChatMessage(connection, time);
			
		}
		/*
		 * CHANGE CHAT MODE
		 */
		else if (tag.equalsIgnoreCase("NOTICE")) {
			String messagePrint = mes.substring(mes.indexOf(msgSplit) + msgSplit.length());
			String id = data.get("msg-id");
			boolean enabled = true;
			ChatMode mode = ChatMode.OTHER;
			if (id.contains("off")) 
				enabled = false;
			
			// SUB MODE
			if (id.contains("subs_")) 
				mode = ChatMode.SUB_ONLY;
			
			// EMOTE ONLY MODE
			else if (id.contains("emote_only_")) 
				mode = ChatMode.EMOTE_ONLY;
			
			// FOLLOWERS ONLY MODE
			else if (id.contains("followers_")) 
				mode = ChatMode.FOLLOWER_ONLY;
			
			// SLOW MODE
			else if (id.contains("slow_")) 
				mode = ChatMode.SLOW;
			
			// R9K MODE
			else if (id.contains("r9k_")) 
				mode = ChatMode.R9K;
			
			message = new ChatModeMessage(connection, time, mode, messagePrint, enabled);
		}
		/*
		 * NORMAL CHAT MESSAGE
		 */
		else if (tag.equalsIgnoreCase("PRIVMSG")) {
			// GETS SENDER
			String temp = "";
			String username = data.get("display-name").toLowerCase();
			// no display name
			if (username == null || username.equals("")) {
				temp = data.get("user-type");
				username = temp.substring(temp.indexOf(':') + 1, temp.indexOf('!')).toLowerCase();
			}
			user = getUser(connection, username);

			// BADGES
			String badges = data.get("badges");
			if (!badges.equals("")) {
				ArrayList<String> badgeList = new ArrayList<String>(Arrays.asList(badges.split(",")));
				for (String badge : badgeList) {
					int splitLoc = badge.indexOf('/');
					BadgeType type = connection.getBadge(badge.substring(0, splitLoc), badge.substring(splitLoc + 1));
					if (type == null) 
						continue;
					user.addType(type);
				}
			}
			// COLOR
			if (data.get("color").length() != 0) 
				user.setDisplayColor(data.get("color"));
			
			// DISPLAY NAME
			if (!data.get("display-name").equals("")) 
				user.setDisplayName(data.get("display-name"));
			
			// USER ID
			if (!data.get("user-id").equals("")) 
				user.setUserID(Long.parseLong(data.get("user-id")));
			
			// CHECKS IF BITS WERE SENT
			ChatMessage finalMessage = null;
			if (data.containsKey("bits")) 
				finalMessage = new BitMessage(connection, time, user, mes.substring(tags.length() + 3),
						Integer.parseInt(data.get("bits")));
			else
				finalMessage = new ChatMessage(connection, time, user, mes.substring(tags.length() + 3));
			
			// EMOTES
			HashSet<String> set = new HashSet<String>();
			//TWITCH
			if (!data.get("emotes").equals("")) {
				String[] emotes = data.get("emotes").split("/");
				for (String emote : emotes) {
					int id = Integer.parseInt(emote.substring(0, emote.indexOf(":")));
					String[] occur = emote.substring(emote.indexOf(":") + 1).split(",");
					for (String occure : occur) {
						String[] index = occure.split("-");
						finalMessage.addEmote(new ChatEmote(finalMessage.getMessage(), EmoteType.TWITCH, id+"", Integer.parseInt(index[0]), Integer.parseInt(index[1])));
						set.add(mes.substring(tags.length() + 3).substring(Integer.parseInt(index[0]), Integer.parseInt(index[1])+1));
					}
				}
			}
			//OTHER
			for(EmoteType type : EmoteType.values()){
				if(type!=EmoteType.TWITCH){
					for(Emote emote : emoteManager.getEmoteList(type).values()){
						if(!set.contains(emote.getName())){
							finalMessage.addEmotes(findChatEmote(mes.substring(tags.length() + 3), emote));
							set.add(emote.getName());
						}
					}
				}
			}
			message = finalMessage;
		} else if (tag.equalsIgnoreCase("ROOMSTATE")) {

		}
		// SUB NOTIFICATIONS
		else if (tag.equalsIgnoreCase("USERNOTICE")) {
			String sys_msg = replaceSpaces(data.get("system-msg"));
			String messagePrint = "", plan = data.get("msg-param-sub-plan"), type = data.get("msg-id");
			int loc = mes.indexOf(msgSplit) + msgSplit.length() + 2;
			if (loc < mes.length() && loc > 0) 
				messagePrint = mes.substring(loc);
			user = getUser(connection, data.get("login"));
			int length = -1;
			if (data.get("msg-param-months").length() == 1) 
				length = Integer.parseInt(data.get("msg-param-months"));
			message = new SubscriberMessage(connection, user, time, sys_msg, messagePrint, length, plan, type);
		} else if (tag.equalsIgnoreCase("USERSTATE")) {

		} else {

		}
		//If message was successfully parsed, process the message too the GUI
		if (message != null) {
			connection.getMessageProcessor().process(message);
			finished(message);
		}
	}

	//Calls chat message listener
	public void finished(Message message) {
		if (message instanceof ChatMessage) {
			InternalListeners.checkCommand((ChatMessage) message);
		}
	}
	
	//Locates the list of chat emotes
	public ArrayList<ChatEmote> findChatEmote(String message, Emote emote){
		ArrayList<ChatEmote> list = new ArrayList<ChatEmote>();
		if(message.contains(emote.getName())){
			int lastIndex = 0;
			while(lastIndex != -1){
			    lastIndex = message.indexOf(emote.getName(),lastIndex);
			    if(lastIndex != -1){
			    	boolean foundEmote = false;
			    	
			    	if(lastIndex==0){
			    		if(message.equals(emote.getName()) || message.charAt(emote.getName().length())==' ')
			    			foundEmote = true;
			    	}
			    	else if(lastIndex+emote.getName().length()==message.length()){
			    		if(message.charAt(lastIndex-1)==' ')
			    			foundEmote = true;
			    	}
			    	else{
			    		if(message.charAt(lastIndex-1)==' ' && message.charAt(lastIndex+emote.getName().length())==' ')
			    			foundEmote = true;
			    	}
			    	if(foundEmote)
			    		list.add(new ChatEmote(message, emote.getEmoteType(), emote.getID(), lastIndex, lastIndex+(emote.getName().length()-1)));
			    	lastIndex+=1;
			    }
			}
		}
		return list;
	}

}
