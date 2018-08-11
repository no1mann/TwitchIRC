package twitch.message.types;

import twitch.connection.ChatConnection;
import twitch.user.User;

/*
 * User is banned from the chat
 */
public class BanMessage extends BroadcastMessage{

	private User bannedUser;
	private int amount;
	
	/*
	 * ChatConnection connection: Twitch channel the timeout occurred in
	 * long time: The time the timeout occurred
	 * User banned: The user that was sent to timeout
	 * int amount: The amount of time the timeout lasts 
	 */
	public BanMessage(ChatConnection connection, long time, User banned, int amount) {
		super(connection, time);
		this.bannedUser = banned;
		this.amount = amount;
		setMessage();
	}
	
	/*
	 * ChatConnection connection: Twitch channel the ban occurred in
	 * long time: The time the ban occurred
	 * User banned: The user that was banned
	 */
	public BanMessage(ChatConnection connection, long time, User banned) {
		super(connection, time);
		this.bannedUser = banned;
		this.amount = -1;
		setMessage();
	}
	
	//The message corresponding too the correct ban
	private void setMessage(){
		if(amount == -1)
			super.setMessage(bannedUser.getUsername() + " has been banned from this room.");
		else{
			if(amount == 1)
				super.setMessage(bannedUser.getUsername() + " has been purged.");
			else
				super.setMessage(bannedUser.getUsername() + " has been timed out for " + amount + "s.");
		}
	}
	
	public User getBannedUser(){
		return bannedUser;
	}
	
	public int getBanLength(){
		return amount;
	}
	
	public boolean isBanned(){
		return amount == -1;
	}

}
