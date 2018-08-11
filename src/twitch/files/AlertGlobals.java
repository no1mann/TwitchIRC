package twitch.files;

import java.awt.Color;

public class AlertGlobals {
	
	public static boolean ENABLE_ALERTS = true;
	public static int ALERTS_WIDTH = 1280;
	public static int ALERTS_HEIGHT = 720;
	
	public static boolean FOLLOW_ENABLED = true;
	public static String FOLLOW_IMAGE;
	public static String FOLLOW_AUDIO;
	public static String FOLLOW_TEXT = "{user} has just followed!";
	public static Color FOLLOW_USER_COLOR = Color.decode("#2753e5");
	public static Color FOLLOW_TEXT_COLOR = Color.decode("#ffffff");
	public static int FOLLOW_ALERT_LENGTH = 5;
	
	public static boolean SUB_ENABLED = true;
	public static String SUB_IMAGE;
	public static String SUB_AUDIO;
	public static String SUB_TEXT = "{user} has just subscribed!";
	public static String RESUB_TEXT = "{user} has just resubscribed for {length} months in a row!";
	public static boolean RESUB_TTS = true;
	public static Color SUB_USER_COLOR = Color.decode("#2753e5");
	public static Color SUB_LENGTH_COLOR = Color.decode("#2753e5");
	public static Color SUB_TEXT_COLOR = Color.decode("#ffffff");
	public static int SUB_ALERT_LENGTH = 10;
	
	public static boolean HOST_ENABLED = true;
	public static String HOST_IMAGE;
	public static String HOST_AUDIO;
	public static String HOST_TEXT = "Thank you {user} for hosting with {amount} of viewers!";
	public static Color HOST_USER_COLOR = Color.decode("#2753e5");
	public static Color HOST_AMOUNT_COLOR = Color.decode("#2753e5");
	public static Color HOST_TEXT_COLOR = Color.decode("#ffffff");
	public static int HOST_MINIMUM = 4;
	public static int HOST_ALERT_LENGTH = 6;
	
	public static boolean BITS_ENABLED = true;
	public static String BITS_TEXT = "{user} just cheered {amount} bits!";
	public static boolean BITS_TTS = true;
	public static Color BITS_USER_COLOR = Color.decode("#2753e5");
	public static Color BITS_AMOUNT_COLOR = Color.decode("#2753e5");
	public static Color BITS_TEXT_COLOR = Color.decode("#ffffff");
	public static int BITS_MINIMUM = 100;
	public static int BITS_ALERT_LENGTH = 6;

}
