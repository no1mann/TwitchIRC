package twitch.gui.windows;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import twitch.channels.ChannelInfo;
import twitch.channels.GameInfo;
import twitch.channels.StreamInfo;
import twitch.connection.ConnectionList;
import twitch.connection.SearchType;
import twitch.connection.URLLoader;
import twitch.gui.Globals;
import twitch.gui.panels.ChannelInfoPanel;
import twitch.gui.panels.GameInfoPanel;
import twitch.gui.panels.JPanelList;
import twitch.gui.panels.StreamThumbnailPanel;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;

public class SearchWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1241918649583982885L;
	private JPanel contentPane;
	private JTextField txtSearchField;
	private JPanelList searchList;
	@SuppressWarnings("rawtypes")
	private JComboBox boxType;
	private JLabel lblLoading;
	
	private static String[] searchTypes;
	
	static{
		searchTypes = new String[SearchType.values().length];
		int i = 0;
		for(SearchType type : SearchType.values()){
			searchTypes[i] = type.name();
			i++;
		}
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SearchWindow() {
		setResizable(false);
		setBounds(100, 100, 490, 900);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setTitle("Search");
		
		boxType = new JComboBox();
		for(String type : searchTypes){
			boxType.addItem(type);
		}
		
		txtSearchField = new JTextField();
		txtSearchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_ENTER){
					new Thread(new Runnable(){
						@Override
						public void run() {
							newSearch();
						}
					}, "SearchThread").start();
				}
			}
		});
		txtSearchField.setColumns(10);
		
		searchList = new JPanelList();
		
		lblLoading = new JLabel("");
		lblLoading.setIcon(URLLoader.getImageFromFile(Globals.LOADING_ICON));
		lblLoading.setVisible(false);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(boxType, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(txtSearchField, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(lblLoading, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
						.addComponent(searchList, GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(boxType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtSearchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblLoading))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(searchList, GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
	
	private void newSearch(){
		lblLoading.setVisible(true);
		SearchType type = SearchType.valueOf((String)boxType.getSelectedItem());
		
		String URL = Globals.SEARCH(type, txtSearchField.getText(), 0, 10);
		JSONObject obj = URLLoader.getKrakenAPI(URLLoader.formatString(URL), false, true);
		if(obj == null){
			return;
		}
		if(type.equals(SearchType.STREAMS)){
			newStreamSearch(obj);
		}
		else if(type.equals(SearchType.GAMES)){
			newGameSearch(obj);
		}
		else if(type.equals(SearchType.CHANNELS)){
			newChannelSearch(obj);
		}
		lblLoading.setVisible(false);
	}
	
	private void newStreamSearch(JSONObject obj){
		JSONArray array = (JSONArray)obj.get("streams");
		searchList.clearPanels();
		for(int i = array.size()-1; i >= 0; i--){
			JSONObject data = (JSONObject)array.get(i);
			String channel = (String) ((JSONObject)(data).get("channel")).get("name");
			StreamThumbnailPanel panel = new StreamThumbnailPanel(new StreamInfo(channel, data));
			panel.addMouseListener(new MouseAdapter(){
				@Override
				public void mousePressed(MouseEvent arg0) {
					ConnectionList.setupNewConnection(((StreamThumbnailPanel)arg0.getComponent()).getStreamInfo().getName(), true);
				}
			});
			searchList.addPanel(panel, false, 5);
		}
		searchList.redraw();
	}
	
	private void newChannelSearch(JSONObject obj){
		JSONArray array = (JSONArray)obj.get("channels");
		searchList.clearPanels();
		for(int i = array.size()-1; i >= 0; i--){
			JSONObject data = (JSONObject)array.get(i);
			ChannelInfo info = new ChannelInfo();
			info.parseData(data);
			ChannelInfoPanel panel = new ChannelInfoPanel(info, 370);
			panel.addMouseListener(new MouseAdapter(){
				@Override
				public void mousePressed(MouseEvent arg0) {
					ConnectionList.setupNewConnection(((ChannelInfoPanel)arg0.getComponent()).getChannelInfo().getName(), true);
				}
			});
			searchList.addPanel(panel, false, 5);
		}
		searchList.redraw();
	}
	
	private void newGameSearch(JSONObject obj){
		JSONArray array = (JSONArray)obj.get("games");
		searchList.clearPanels();
		for(int i = array.size()-1; i >= 0; i--){
			JSONObject data = (JSONObject)array.get(i);
			GameInfoPanel panel = new GameInfoPanel(new GameInfo(data));
			panel.addMouseListener(new MouseAdapter(){
				@Override
				public void mousePressed(MouseEvent arg0) {
					new Thread(new Runnable(){
						@Override
						public void run() {
							txtSearchField.setText(((GameInfoPanel)arg0.getComponent()).getGameInfo().getName());
							boxType.setSelectedItem(SearchType.STREAMS.name());
							newSearch();
						}
					}, "SearchThread").start();
				}
			});
			searchList.addPanel(panel, false);
		}
		searchList.redraw();
	}
}
