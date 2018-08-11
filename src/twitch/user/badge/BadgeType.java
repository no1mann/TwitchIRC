package twitch.user.badge;

import twitch.connection.URLLoader;
import twitch.gui.AttributeList;
import twitch.gui.ClickableType;
import twitch.gui.Globals;
import twitch.gui.MutableAttributeSet;

/*
 * Badge class used as a global reference for a single badge. Stores the image and metadata
 */
public class BadgeType implements Comparable<BadgeType>{
	
	//Metadata
	private String apiName, version, name, description;
	private GlobalBadges badge = GlobalBadges.OTHER;
	//Image
	private MutableAttributeSet att;
	
	/*
	 * String apiName: badge type
	 * String version: version number of the badge
	 * String name: name of the badge
	 * String description: description of the badge
	 * String imageUrl: URL of the badge image
	 * boolean isURL: true if URL, false if file in directory
	 */
	public BadgeType(String apiName, String version, String name, String description, String imageUrl, boolean isURL){
		this.version = version;
		this.apiName = apiName;
		this.name = name;
		this.description = description;
		att = AttributeList.getImageAttribute();
		
		//Load image from api type
		if(apiName.equals("ffz"))
			att.setImage(URLLoader.getImageFromFile(Globals.FFZ_ICON));
		else if(!isURL)
			att.setImage(URLLoader.getImageFromFile(imageUrl));
		else
			att.setImage(URLLoader.getImageFromURL(imageUrl));
		
		//Gets badge type for badge with similar names
		if(apiName.equalsIgnoreCase("bits"))
			try{
				badge = GlobalBadges.valueOf(apiName.toUpperCase() + "_" + version);
			} catch (Exception e){
				badge = GlobalBadges.OTHER;
			}
		else if(apiName.equalsIgnoreCase("subscriber"))
			try{
				badge = GlobalBadges.valueOf(apiName.toUpperCase() + "_" + version);
			} catch (Exception e){
				badge = GlobalBadges.OTHER;
			}
		else{
			try{
				badge = GlobalBadges.valueOf(apiName.toUpperCase());
			} catch (Exception e){
				badge = GlobalBadges.OTHER;
			}
		}
		att.setClickable(ClickableType.BADGE, this);
	}
	
	public String getVersion() {
		return version;
	}

	public String getAPIName() {
		return apiName;
	}
	
	public String getName(){
		return name;
	}
	
	public String getDescription(){
		return description;
	}
	
	public MutableAttributeSet getAttribute(){
		return att;
	}
	
	public int hashCode(){
		return apiName.hashCode() + version.hashCode();
	}
	
	public String toString(){
		return apiName + ", " + version;
	}
	
	public boolean equals(BadgeType type){
		if(type != null)
			return type.getAPIName().equals(apiName) && type.getVersion().equals(version);
		
		return false;
	}
	
	public GlobalBadges getBadge(){
		return badge;
	}

	@Override
	public int compareTo(BadgeType badgeType) {
		if(badge == null && badgeType.getBadge() == null)
			return 0;
		else if(badge == null)
			return 1;
		else if(badgeType.getBadge() == null)
			return -1;
		else
			return badge.compareTo(badgeType.getBadge());
	}
}
