package twitch.files;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.esotericsoftware.minlog.Log;

/*
 * Basic file manager for configurations
 * Uses key value pair data structure
 */
public class Database extends FileManager{

	//Stores the key, value pair for configuration file
	private ConcurrentHashMap<String, String> data = new ConcurrentHashMap<String, String>();
	
	public Database(String location){
		this(new File(location));
	}
	
	public Database(File location){
		super(location);
		loadData();
	}
	
	//Load data from file
	private void loadData(){
		Log.info("Loading database info: " + super.getFileName());
		for(String line : super.getLines()){
			if(line.length() == 0)
				continue;
			
			String[] split = line.split(": ");
			if(split.length == 2)
				data.put(split[0], split[1]);
			else{
				String value = "";
				for(int i = 1; i < split.length; i++)
					value+=split[i]+": ";
				data.put(split[0], value);
			}
		}
	}
	
	//Save data to file
	@Override
	public void save(){
		ArrayList<String> output = new ArrayList<String>();
		for(String key: data.keySet())
			output.add(key + ": " + data.get(key));
		super.setLines(output);
		super.save();
	}
	
	//Adds new configuration
	public void put(String key, String value){
		data.put(key, value);
	}
	
	public String get(String key){
		if(!data.containsKey(key) || data.get(key).equals("")){
			put(key, "");
			return null;
		}
		return data.get(key);
	}
	
	public boolean contains(String key){
		return data.containsKey(key);
	}
}
