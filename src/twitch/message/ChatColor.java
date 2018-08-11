package twitch.message;

import twitch.utils.RandomColor;

/*
 * Manages random chat color for username
 */
public class ChatColor{
	
	private static boolean shouldChange = false;
	
	public static void enable(){
		shouldChange = true;
	}
	
	public static void disable(){
		shouldChange = false;
	}

	public static void onChatMessage() {
		if(shouldChange){
			ModerationTools.color(RandomColor.getRandomColor());
		}
	}
}
