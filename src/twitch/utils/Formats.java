package twitch.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Standard date and decimal formats
 */
public class Formats {

	public static final DecimalFormat numberCommaFormatter = new DecimalFormat("#,###");
	public static final SimpleDateFormat origDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	public static final SimpleDateFormat newDateFormatter = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a");
	public static final SimpleDateFormat logDateFormatter = new SimpleDateFormat("MM-dd-yyyy");
	public static final SimpleDateFormat chatDateFormatter = new SimpleDateFormat("hh:mm:ss");//dd/MM/yyyy
	
	
	public static String getCurrentTimeStamp(long time) {
	    Date now = new Date(time);
	    return chatDateFormatter.format(now);
	}
	
	public static String formatChannel(String channel){
		if(channel.startsWith("#")){
			return channel;
		}
		return "#" + channel;
	}
}
