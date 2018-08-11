package twitch.files;

public enum ConfigName {
	SERVER("server.ini"),
	LOGIN("login.ini"),
	ALERTS("alerts.ini"),
	CHANNELS("channels.txt"),
	AUTOCONNECT("autoconnect.txt"),
	SELECTED("selected.txt"),;
	
	private String file;
	
	ConfigName(String file){
		this.file = file;
	}
	
	public String getFileName(){
		return file;
	}
}
