package twitch.channels;

import org.json.simple.JSONObject;

import twitch.connection.URLLoader;
import twitch.gui.Globals;

public class StreamInfo extends ChannelInfo{

	private String liveTime;
	private long streamID, viewers, videoHeight, delay;
	private double fps;
	private boolean isPlaylist, isLive = false;
	
	//Called if new stream
	public StreamInfo(String channel){
		super();
		super.setChannel(channel);
		reloadData();
	}
	
	//Called if stream is already a channel
	public StreamInfo(String channel, JSONObject data){
		super();
		super.setChannel(channel);
		parseStreamData(data, true);
	}
	
	//Fetches updates to stream data
	public void reloadData(){
		try {
			JSONObject data = URLLoader.getKrakenAPI(Globals.STREAM_DATA(getChannel()), false, true);
			this.parseStreamData(data, false);
			data = null;
		} catch (Exception e) {
			return;
		}
	}
	
	private void parseStreamData(JSONObject data, boolean skip){
		//Runs only the first time - Gets raw stream data
		if (!skip) {
			try {
				data = (JSONObject) data.get("stream");
				if (data == null) {
					throw new Exception("Stream data is null");
				}
			} catch (Exception e) {
				isLive = false;
				return;
			}
		}
		//Is channel live
		isLive = true;
		
		//IS THE STREAM A PLAYLIST
		try{
			isPlaylist = (boolean) data.get("is_playlist");
		} catch(Exception e){
			isPlaylist = false;
		}
		
		//STREAM START TIME
		try{
			liveTime = (String)data.get("created_at");
		} catch(Exception e){
			liveTime = "";
		}
		
		//STREAM DELAY
		try{
			delay = (long)data.get("delay");
		} catch(Exception e){
			delay = 0;
		}
		
		//CURRENT LIVE VIEWERS
		try{
			viewers = (long)data.get("viewers");
		} catch(Exception e){
			viewers = 0;
		}
		
		//STREAM ID
		try{
			streamID = (long)data.get("_id");
		} catch(Exception e){
			streamID = 0;
		}
		
		//STREAM VIDEO RESOLUTION
		try{
			videoHeight = (long) data.get("video_height");
		} catch(Exception e){
			videoHeight = 0;
		}
		
		//STREAM FPS
		try{
			fps = (double)data.get("average_fps");
		} catch(Exception e){
			fps = 0;
		}
		
		//PARSES CHANNEL DATA
		this.parseData((JSONObject)data.get("channel"));
		//Clears data for memory reasons
		data.clear();
	}

	public String getLiveTime() {
		return liveTime;
	}

	public long getStreamID() {
		return streamID;
	}

	public long getViewers() {
		return viewers;
	}

	public long getVideoHeight() {
		return videoHeight;
	}

	public double getFps() {
		return fps;
	}

	public long getDelay() {
		return delay;
	}

	public boolean isPlaylist() {
		return isPlaylist;
	}

	public boolean isLive() {
		return isLive;
	}
	
}
