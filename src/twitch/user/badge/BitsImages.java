package twitch.user.badge;

import java.awt.Color;
import java.util.HashMap;

import twitch.connection.URLLoader;
import twitch.gui.AttributeList;
import twitch.gui.Globals;
import twitch.gui.MutableAttributeSet;

public class BitsImages{
	private static HashMap<Integer, MutableAttributeSet> bitsList;
	private static final int[] BITS = {1, 100, 1000, 5000, 10000, 100000};
	private static final Color[] TEXT_COLOR = {
		Color.decode("#979797"),
		Color.decode("#9c3ee8"),
		Color.decode("#1db2a5"),
		Color.decode("#0099fe"),
		Color.decode("#f43021"),
		Color.decode("#ffbd20")
	};
	
	static{
		bitsList = new HashMap<Integer, MutableAttributeSet>();
		MutableAttributeSet att = null;
		for(int bit : BITS){
			att = AttributeList.getImageAttribute();
			att.setImage(URLLoader.getImageFromURL(Globals.BITS_IMAGE_URL(bit)));
			bitsList.put(bit, att);
		}
	}
	
	public static MutableAttributeSet getBitsAttribute(int amount){
		if(amount<100){
			return bitsList.get(1);
		}
		else if(amount<1000){
			return bitsList.get(100);
		}
		else if(amount<5000){
			return bitsList.get(1000);
		}
		else if(amount<10000){
			return bitsList.get(5000);
		}
		else if(amount<100000){
			return bitsList.get(10000);
		}
		else{
			return bitsList.get(100000);
		}
	}
	
	public static Color getBitsColor(int amount){
		if(amount<100){
			return TEXT_COLOR[0];
		}
		else if(amount<1000){
			return TEXT_COLOR[1];
		}
		else if(amount<5000){
			return TEXT_COLOR[2];
		}
		else if(amount<10000){
			return TEXT_COLOR[3];
		}
		else if(amount<100000){
			return TEXT_COLOR[4];
		}
		else{
			return TEXT_COLOR[5];
		}
	}
}
