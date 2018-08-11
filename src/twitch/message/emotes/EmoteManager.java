package twitch.message.emotes;

import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import twitch.connection.URLLoader;
import twitch.gui.AttributeList;
import twitch.gui.ClickableType;
import twitch.gui.Globals;
import twitch.gui.MutableAttributeSet;

/*
 * Global emote manager for tracking and storing a single, global instance of each emote
 */
public class EmoteManager {
	
	/*
	 * Stores all emotes:
	 * Key: EmoteType (Twitch, BTTV, FFZ)
	 * Value: HashMap of all emotes (Key: Emote ID, Value: Emote Data)
	 */
	
	private static ConcurrentHashMap<EmoteType, ConcurrentHashMap<String, Emote>> emoteList;
	
	public EmoteManager(){
		//Instantiates data structures for storing emotes
		emoteList = new ConcurrentHashMap<EmoteType, ConcurrentHashMap<String, Emote>>();
		for(EmoteType type : EmoteType.values())
			emoteList.put(type, new ConcurrentHashMap<String, Emote>());
		
		//Start up thread for loading global emotes (Used for loading 3rd party emotes)
		new Thread(new Runnable(){
			@Override
			public void run() {
				//BTTV GLOBAL
				JSONObject obj = URLLoader.getJSONFromURL(Globals.BTTV_GLOBALS);
				JSONArray array = (JSONArray) obj.get("emotes");
				loadBTTVEmotes(array, null);
				//FFZ GLOBAL
				obj = URLLoader.getJSONFromURL(Globals.FFZ_GLOBALS);
				obj = (JSONObject) obj.get("sets");
				for(Object setList : obj.values()){
					Object listData = ((JSONObject)setList).get("emoticons");
					loadFFZEmotes((JSONArray)listData, null);
				}
			}
		}, "EmoteGlobalLoaderThread").start();
	}
	
	//Loads emotes for a specific Twitch channel (Used for loading 3rd party emotes)
	public void loadChannel(String channel){
		new Thread(new Runnable(){
			@Override
			public void run() {
				//BTTV CHANNEL
				JSONObject obj = URLLoader.getJSONFromURL(Globals.BTTV_CHANNELS(channel));
				if(obj==null)
					return;
				
				JSONArray array = (JSONArray) obj.get("emotes");
				loadBTTVEmotes(array, channel);
				//FFZ CHANNEL
				try{
					obj = URLLoader.getJSONFromURL(Globals.FFZ_CHANNELS(channel));
					obj = (JSONObject) obj.get("sets");
					for(Object setList : obj.values()){
						Object listData = ((JSONObject)setList).get("emoticons");
						loadFFZEmotes((JSONArray)listData, channel);
					}
				} catch(Exception e){
					return;
				}
			}
		}, "EmoteChannelLoaderThread").start();
	}
	
	//Loads FFZ emotes from a specific channel
	private void loadFFZEmotes(JSONArray array, String global){
		//Cycles through every emote from API
		for(Object emoteData : array){
			JSONObject rawEmote = (JSONObject)emoteData;
			String id = (long)rawEmote.get("id") + "";
			String name = (String) rawEmote.get("name");
			loadEmote(EmoteType.FFZ, name, id);
			String creator = null;
			try{
				creator = (String) ((JSONObject)rawEmote.get("owner")).get("name");
			} catch (Exception e){
				creator = "Global";
			}
			if(global!=null)
				getEmote(EmoteType.FFZ, id).setChannel(global);
			getEmote(EmoteType.FFZ, id).setCreator(creator);
		}
	}
	
	//Loads BTTV emotes from a specific channel
	private void loadBTTVEmotes(JSONArray array, String global){
		//Cycles through every emote from API
		for(Object emoteData : array){
			JSONObject rawEmote = (JSONObject)emoteData;
			String id = (String) rawEmote.get("id");
			String name = (String) rawEmote.get("code");
			loadEmote(EmoteType.BTTV, name, id);
			String creator = null;
			try{
				creator = (String) rawEmote.get("channel");
			} catch (Exception e){
				creator = "Global";
			}
			if(global!=null)
				getEmote(EmoteType.BTTV, id).setChannel(global);
			getEmote(EmoteType.BTTV, id).setCreator(creator);
		}
	}
	
	//If the emote already exists
	public boolean hasEmote(EmoteType type, String id){
		return emoteList.get(type).containsKey(id);
	}
	
	//Loads a single emote from the API
	public void loadEmote(EmoteType type, String name, String id){
		//If emote already exists, ignore
		if(emoteList.containsKey(id))
			return;
		//Gets the image from specific URL
		MutableAttributeSet tempAtt = AttributeList.getImageAttribute();
		String URL = null;
		if(type == EmoteType.TWITCH)
			URL = Globals.EMOTE_IMAGE(id);
		else if(type == EmoteType.BTTV)
			URL = Globals.BTTV_EMOTE(id);
		else if(type == EmoteType.FFZ)
			URL = Globals.FFZ_EMOTE(id);
		tempAtt.setImage(URLLoader.getImageFromURL(URL));
		//Loads the image into the emotelist
		emoteList.get(type).put(id, new Emote(type, name, id, tempAtt));
		//Sets clickable property for the emote
		tempAtt.setClickable(ClickableType.EMOTE, emoteList.get(type).get(id));
	}
	
	public Emote getEmote(EmoteType type, String id){
		return emoteList.get(type).get(id);
	}
	
	//Get list of all emotes
	public ConcurrentHashMap<String, Emote> getEmoteList(EmoteType type){
		return emoteList.get(type);
	}
	
}
