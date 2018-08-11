package twitch.user.badge;

import java.awt.Color;

import twitch.gui.AttributeList;
import twitch.gui.MutableAttributeSet;

/*
 * List of standard badges.
 * This is not an exhaustive list since badges are constantly being added.
 * Badges not listed here get the enum value as OTHER
 */
public enum GlobalBadges {
	BROADCASTER(new Color(222, 28, 28), "B"),
	STAFF(new Color(61, 31, 98), "ST"),
	ADMIN(new Color(254, 173, 27), "A"),
	GLOBAL_MOD(new Color(0, 111, 32), "GM"),
	APP_DEV(Color.BLUE, "DEV"),
	MODERATOR(new Color(52, 174, 10), "M"),
	SUBSCRIBER_36(Color.YELLOW, "S36"),
	SUBSCRIBER_24(Color.YELLOW, "S24"),
	SUBSCRIBER_12(Color.YELLOW, "S12"),
	SUBSCRIBER_6(Color.YELLOW, "S6"),
	SUBSCRIBER_3(Color.YELLOW, "S3"),
	SUBSCRIBER_1(Color.YELLOW, "S1"),
	SUBSCRIBER_0(Color.YELLOW, "S"),
	PARTNER(new Color(100, 67, 171), "V"),
	BITS_1000000(new Color(253, 202, 16), "1M"),
	BITS_900000(new Color(253, 202, 16), "900K"),
	BITS_800000(new Color(253, 202, 16), "800K"),
	BITS_700000(new Color(253, 202, 16), "700K"),
	BITS_600000(new Color(253, 202, 16), "600K"),
	BITS_500000(new Color(253, 202, 16), "500K"),
	BITS_400000(new Color(253, 202, 16), "400K"),
	BITS_300000(new Color(253, 202, 16), "300K"),
	BITS_200000(new Color(253, 202, 16), "200K"),
	BITS_100000(new Color(253, 202, 16), "100K"),
	BITS_75000(new Color(255, 39, 31), "75K"),
	BITS_50000(new Color(255, 39, 31), "50K"),
	BITS_25000(new Color(255, 39, 31), "25K"),
	BITS_10000(new Color(255, 39, 31), "10K"),
	BITS_5000(new Color(72, 172, 254), "5K"),
	BITS_1000(new Color(61, 216, 179), "1K"),
	BITS_100(new Color(201, 126, 255), "100"),
	BITS_1(new Color(202, 199, 206), "1"),
	TURBO(new Color(100, 67, 171), "T"),
	PREMIUM(new Color(2, 156, 220), "P"),
	FFZ(new Color(117,80, 0), "F"),
	OTHER(Color.WHITE, "O");
	
	private Color color;
	private String shortName;
	private MutableAttributeSet att;
	
	GlobalBadges(Color color, String shortName){
		this.color = color;
		this.shortName = shortName;
		att = AttributeList.getBroadcastAttribute();
		att.setColor(color);
	}
	
	public Color getColor(){
		return color;
	}
	
	public String getShortName(){
		return shortName;
	}
	
	public String getLongName(){
		return this.name();
	}
	
	public MutableAttributeSet getAttribute(){
		return att;
	}
}
