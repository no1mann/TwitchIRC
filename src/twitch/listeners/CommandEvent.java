package twitch.listeners;

import twitch.user.User;

public class CommandEvent {

	private User sender;
	private String message;
	
	public CommandEvent(User sender, String message){
		this.sender = sender;
		this.message = message;
	}
	
	public User getSender(){
		return sender;
	}
	
	public String getMessage(){
		return message;
	}
	
}
