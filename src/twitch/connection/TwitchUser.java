package twitch.connection;

import org.jibble.pircbot.PircBot;

import com.esotericsoftware.minlog.Log;

/*
 * Twitch User for sending chat message, NOT RECEIVING
 */
public class TwitchUser extends PircBot{
	
	public static final int GLOBAL_LIMIT = 1;//(30000/20) + 2000;
	public static long timeSinceMessage = System.currentTimeMillis();
	
	public TwitchUser(String name){
		this.setName(name);
	}

	//Sends message to Twitch channel
	public void postInChat(String channel, String message){
		if((System.currentTimeMillis()-timeSinceMessage)>=GLOBAL_LIMIT){
			Log.info("Posted to " + channel + ": " + message);
			if(ConnectionList.getChannel(channel) != null){
				ConnectionList.getChannel(channel).addLastMessage(message);
			}
			this.sendMessage(channel, message);
			timeSinceMessage = System.currentTimeMillis();
		}
	}
	
}
