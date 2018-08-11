package twitch.files.labels;

import twitch.channels.ChannelInfo;
import twitch.connection.URLLoader;
import twitch.files.*;
import twitch.gui.Globals;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.esotericsoftware.minlog.Log;

/*
 * Controls labels for streaming - Stores current sub, follow count, and more to text document for OBS
 * 
 * Currently work in progress
 */
public class LabelManager{

	private static boolean runThreads = false;
	private static Thread pollThread;
	private static ChannelInfo user;
	
	private static String labelsLocation = Globals.FILES_LOCATION + "\\labels\\";
	private static ConcurrentHashMap<Labels, FileManager> labels = new ConcurrentHashMap<Labels, FileManager>();
	
	static{
		Log.info("Label Manager instantiated");
		new Thread(new Runnable(){
			@Override
			public void run() {
				init();
				startPolls();
			}
		}).start();
	}
	
	//Loads label polling
	private static void init(){
		user = new ChannelInfo(Globals.USERNAME);
		createDir();
		for(Labels label : Labels.values()){
			labels.put(label, new FileManager(labelsLocation + label.name() + ".txt"));
		}
		pollThread = new Thread("LabelsPollingThread"){
			public void run() {
				while (runThreads) {
					try {
						// Follow Count Total
						JSONObject obj = URLLoader.getKrakenAPI(Globals.CHANNEL_FOLLOWERS(Globals.USERNAME, 1), false,
								true);
						setLabel(Labels.FOLLOW_COUNT_TOTAL, "" + obj.get("_total"));

						// Newest Follower
						JSONObject objName = (JSONObject) ((JSONArray) obj.get("follows")).get(0);
						objName = ((JSONObject) objName.get("user"));
						String name = (String) (objName).get("display_name");
						if (name == null) {
							name = (String) (objName).get("name");
						}
						setLabel(Labels.FOLLOW_LATEST, name);

						//IF USER HAS A SUB BUTTON
						if (user.isPartnered()) {
							// Sub Count Total
							obj = URLLoader.getKrakenAPI(Globals.SUBSCRIPTION_DATA(Globals.USERNAME, 1), true, true);
							setLabel(Labels.SUB_COUNT_TOTAL, "" + obj.get("_total"));

							// Newest Sub
							objName = (JSONObject) ((JSONArray) obj.get("subscriptions")).get(0);
							objName = ((JSONObject) objName.get("user"));
							name = (String) (objName).get("display_name");
							if (name == null) {
								name = (String) (objName).get("name");
							}
							setLabel(Labels.SUB_LATEST, name);
						}
					} catch (Exception e) {
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		};
	}
	
	private static void startPolls(){
		runThreads = true;
		pollThread.start();
	}
	
	@SuppressWarnings("unused")
	private static void stopPolls(){
		runThreads = false;
	}
	
	private static void createDir(){
		File theDir = new File(labelsLocation);
		if (!theDir.exists()) {
		    try{
		        theDir.mkdir();
		    } 
		    catch(SecurityException se){
		    }        
		}
	}
	
	public static void setLabel(Labels label, String value){
		labels.get(label).setLine(0, value);
		labels.get(label).save();
	}
	
	public static String getLabel(Labels label){
		return labels.get(label).getLine(0);
	}
}
