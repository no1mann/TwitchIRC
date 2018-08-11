package twitch.message.types;

import twitch.connection.ChatConnection;
import twitch.gui.AttributeList;
import twitch.gui.ClickableType;
import twitch.gui.MutableAttributeSet;
import twitch.listeners.GlobalListeners;
import twitch.listeners.types.ListenerType;
import twitch.user.User;
import twitch.user.badge.BitsImages;

/*
 * When Bits are sent in chat
 */
public class BitMessage extends ChatMessage{
	
	private int amount;
	private String currency = " bits";
	
	/*
	 * ChatConnection connection: Twitch channel the bits were sent in
	 * long rawTime: The time the bits were send
	 * User user: The user that sent the bits
	 * String message: The message sent with the bits
	 * int amount: Amount of bits sent 
	 */
	public BitMessage(ChatConnection connection, long rawTime, User user, String message, int amount) {
		super(connection, rawTime, user, message);
		this.amount = amount;
	}

	@Override
	public void appendMessage(){
		MutableAttributeSet att = AttributeList.getBitsAttribute();
		//att.setColor(Color.CYAN);
		att.setClickable(ClickableType.USERNAME, super.getSender().getUsername());
		super.printTime(getChatConnection());
		super.printBadges();
		getChatConnection().getStyledChat().appendText(super.getSender().getDisplayName() + " ", att);
		getChatConnection().getStyledChat().appendImage(BitsImages.getBitsAttribute(amount));
		att.setColor(BitsImages.getBitsColor(amount));
		if(amount == 1)
			currency = " bit";
		getChatConnection().getStyledChat().appendText(" " + amount + currency, att);
		super.printMessage();
		getChatConnection().getStyledChat().newLine();
		GlobalListeners.triggerListener(ListenerType.ON_USER_MESSAGE, this);
	}
	
	public int getBitsAmount(){
		return amount;
	}
	
}
