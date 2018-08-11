package twitch.message.types;

import java.awt.Color;

import twitch.connection.ChatConnection;
import twitch.user.User;


public class SubscriberMessage extends BroadcastMessage{
	
	enum SubscriberPlan{
		PRIME,
		ONE,
		TWO,
		THREE,
		OTHER
	}
	
	enum SubscriberType{
		SUB,
		RESUB,
		CHARITY,
		OTHER
	}
	
	private String userMessage;
	private int length;
	private User user;
	private SubscriberPlan subPlan;
	private SubscriberType subType;
	
	/*
	 * ChatConnection connection: Twitch channel the subscription occurred in
	 * User user: The user that subscribed
	 * long time: The time the sub occurred
	 * String sys_msg: The broadcast sub message
	 * String message: The users custom message 
	 * int length: The length of the recurring sub
	 * String plan: The subscriber plan
	 * String type: The subscriber type
	 */
	public SubscriberMessage(ChatConnection connection, User user, long time, String sys_msg, String message, int length, String plan, String type){
		super(connection, time, sys_msg);
		if(message != null && message.length()!=0)
			super.setMessage(sys_msg + ": " + message); 
		this.userMessage = message;
		this.length = length;
		this.user = user;
		parsePlan(plan);
		parseType(type);
		this.setBackgroundColor(Color.DARK_GRAY);
	}
	
	//Parses the string plan
	private void parsePlan(String plan) {
		if (plan == null) {
			subPlan = null;
			return;
		}
		if (plan.equalsIgnoreCase("prime"))
			subPlan = SubscriberPlan.PRIME;

		else if (plan.equalsIgnoreCase("1000"))
			subPlan = SubscriberPlan.ONE;

		else if (plan.equalsIgnoreCase("2000"))
			subPlan = SubscriberPlan.TWO;

		else if (plan.equalsIgnoreCase("3000"))
			subPlan = SubscriberPlan.THREE;

		else
			subPlan = SubscriberPlan.OTHER;
	}
	
	//Parses the string type
	private void parseType(String type) {
		if (type == null) {
			type = null;
			return;
		}
		if (type.equalsIgnoreCase("SUB") || type.equalsIgnoreCase("RESUB") || type.equalsIgnoreCase("CHARITY"))
			subType = SubscriberType.valueOf(type.toUpperCase());

		else
			subType = SubscriberType.OTHER;
	}
	
	public boolean usedPrime(){
		return subPlan == SubscriberPlan.PRIME;
	}
	
	public boolean isResub(){
		return length==-1;
	}
	
	public int getResubLength(){
		return length;
	}
	
	public String getUserMessage(){
		return userMessage;
	}
	
	public User getUserSub(){
		return user;
	}
	
	public SubscriberPlan getSubscriberPlan(){
		return subPlan;
	}
	
	public SubscriberType getSubscriberType(){
		return subType;
	}

}
