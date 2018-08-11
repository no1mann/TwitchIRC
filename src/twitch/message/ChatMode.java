package twitch.message;

public enum ChatMode {
	EMOTE_ONLY("EMOTE ONLY", ""),
	SUB_ONLY("SUB ONLY", ""),
	R9K("R9K", ""),
	SLOW("SLOW", "s"),
	FOLLOWER_ONLY("FOLLOWER ONLY", "m"),
	OTHER("OTHER", "");
	
	private String message;
	private String unit;
	
	ChatMode(String message, String unit){
		this.message = message;
		this.unit = unit;
	}
	
	public String getMessage(){
		return message;
	}
	
	public String getUnit(){
		return unit;
	}
}
