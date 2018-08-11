package twitch.utils;

/*
 * Array helper methods
 */
public class ArrayUtils {

	//Checks if string is contained in string array
	public static boolean contains(String[] list, String item){
		for(String temp : list)
			if(temp.equalsIgnoreCase(item))
				return true;
		return false;
	}
	
}
