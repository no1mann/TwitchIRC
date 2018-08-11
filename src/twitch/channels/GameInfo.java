package twitch.channels;

import javax.swing.ImageIcon;

import org.json.simple.JSONObject;

import twitch.connection.URLLoader;

public class GameInfo {

	private long id, giantbombId, popularity;
	private String name;
	private ImageIcon box;
	
	public GameInfo(JSONObject obj){
		parseData(obj);
	}
	
	
	//Parses Game data from Twitch API
	private void parseData(JSONObject obj){
		
		//GAME IMAGE
		try{
			box = URLLoader.getImageFromURL((String) ((JSONObject)obj.get("box")).get("large"));
		} catch(Exception e){
			box = null;
		}
		
		//GAME ID
		try{
			id = (long) obj.get("_id");
		} catch(Exception e){
			id = 0;
		}
		
		//GAME GIANT BOMB
		try{
			giantbombId = (long) obj.get("gaintbomb_id");
		} catch(Exception e){
			giantbombId = 0;
		}
		
		//GAME VIEWERSHIP
		try{
			popularity = (long) obj.get("popularity");
		} catch(Exception e){
			popularity = 0;
		}
		
		//GAME NAME
		try{
			name = (String) obj.get("name");
		} catch(Exception e){
			name = "";
		}
	}

	public long getId() {
		return id;
	}

	public long getGiantbombId() {
		return giantbombId;
	}

	public long getPopularity() {
		return popularity;
	}

	public String getName() {
		return name;
	}

	public ImageIcon getBox() {
		return box;
	}
	
	
}
