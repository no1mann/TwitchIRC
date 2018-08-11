package twitch.message.emotes;

import javax.swing.Icon;

import twitch.gui.MutableAttributeSet;

/*
 * Emote class used as a global reference for a single emote. Stores the image and metadata
 */
public class Emote {
	
	//Emote metadata
	private String name, channel = "Global", creator, id;
	//Emote image and attributes
	private MutableAttributeSet attribute;
	private EmoteType type;
	
	public Emote(EmoteType type, String name, String id, MutableAttributeSet att){
		this.type = type;
		this.name = name;
		this.id = id;
		this.attribute = att;
	}
	
	public Emote(EmoteType type, String id, MutableAttributeSet att){
		this(type, "", id, att);
	}
	
	public void setChannel(String channel){
		this.channel = channel;
	}
	
	public void setCreator(String creator){
		this.creator = creator;
	}
	
	public String getCreator(){
		return creator;
	}
	
	public String getChannel(){
		return channel;
	}
	
	public EmoteType getEmoteType(){
		return type;
	}
	
	public String getName(){
		return name;
	}
	
	public String getID(){
		return id;
	}
	
	public MutableAttributeSet getAttributeSet(){
		return attribute;
	}
	
	//Gets the image
	public Icon getIcon(){
		return attribute.getIcon();
	}
	
}
