package twitch.utils;

/*
 * Time conversions for seconds (used for timeout messages)
 */
public enum TimeConversion {

	ONE_MIN(60),
	FIVE_MIN(300),
	TEN_MIN(600),
	ONE_HOUR(3600),
	TWELVE_HOUR(43200),
	ONE_DAY(86400),
	ONE_WEEK(604800);
	
	int time;
	
	TimeConversion(int time){
		this.time = time;
	}
	
	public int getSeconds(){
		return time;
	}
	
}
