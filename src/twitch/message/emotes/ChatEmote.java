package twitch.message.emotes;

import twitch.connection.TwitchChat;
import twitch.gui.graphics.EmoteWindow;

/*
 * Chat Emote instance for tracking GUI emotes printed too the screen
 */
public class ChatEmote implements Comparable<ChatEmote>{

	public static EmoteWindow emoteWindow = null;
	
	//Star and end positions in the chat message
	private int start, end;
	//ID of emote
	private String id;
	private EmoteType type;
	
	public ChatEmote(String message, EmoteType type, String id, int start, int end){
		this.id = id;
		this.type = type;
		this.start = start;
		this.end = end;
		
		//If the emote window is enabled, display the emote
		if(emoteWindow != null){
			if(!TwitchChat.emoteManager.hasEmote(type, id))
				TwitchChat.emoteManager.loadEmote(type, message.substring(start, end + 1), id+"");
			emoteWindow.addEmote(TwitchChat.emoteManager.getEmote(type, id));
		}
	}

	public String getId() {
		return id;
	}
	
	public EmoteType getEmoteType(){
		return type;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}
	
	public boolean equals(ChatEmote emote){
		return emote.toString().equals(this.toString());
	}
	
	public String toString(){
		return id + " at " + start + " - " + end;
	}

	@Override
	public int compareTo(ChatEmote arg0) {
		return ((Integer)this.getStart()).compareTo(((Integer)arg0.getStart()));
	}

}
