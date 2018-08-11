package twitch.gui.windows;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import twitch.channels.StreamInfo;
import twitch.channels.ViewCountComparator;
import twitch.connection.ConnectionList;
import twitch.connection.URLLoader;
import twitch.gui.Globals;
import twitch.gui.panels.JPanelList;
import twitch.gui.panels.StreamInfoPanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class FollowedChannelsWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap<String, StreamInfo> streamData = new ConcurrentHashMap<String, StreamInfo>();

	public FollowedChannelsWindow() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				init();
			}
		}, "FollowedChannelsWindowThread").start();
	}
	
	public void init(){
		parseData();
		JPanelList panelList = new JPanelList();
        ArrayList<StreamInfo> list = new ArrayList<StreamInfo>(streamData.values());
        Collections.sort(list, new ViewCountComparator());
		add(panelList);
		setTitle("Followed Channels");
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		for (StreamInfo info : list) {
			StreamInfoPanel panel = new StreamInfoPanel(info, 370);
			panel.addMouseListener(new MouseAdapter(){
				@Override
				public void mousePressed(MouseEvent arg0) {
					ConnectionList.setupNewConnection(((StreamInfoPanel)arg0.getComponent()).getStreamInfo().getName(), true);
					dispose();
				}
			});
			panelList.addPanel(panel, false);
		}
		panelList.redraw();
	}
	
	private void parseData(){
		JSONObject obj = URLLoader.getKrakenAPI(Globals.FOLLOWED_CHANNELS, true, true);
		JSONArray array = (JSONArray)obj.get("streams");
		for(int i = 0; i < (long)obj.get("_total"); i++){
			JSONObject data = (JSONObject)array.get(i);
			String channel = (String) ((JSONObject)(data).get("channel")).get("name");
			streamData.put(channel, new StreamInfo(channel, data));
		}
	}

}
