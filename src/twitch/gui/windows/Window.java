package twitch.gui.windows;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;

import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;

import twitch.channels.StreamListener;
import twitch.connection.ChatConnection;
import twitch.connection.Connection;
import twitch.connection.ConnectionList;
import twitch.connection.ConnectionPopupMenu;
import twitch.connection.TwitchUser;
import twitch.files.Config;
import twitch.files.ConfigName;
import twitch.files.Database;
import twitch.files.FileManager;
import twitch.files.labels.LabelManager;
import twitch.gui.AttributeList;
import twitch.gui.ChannelSortType;
import twitch.gui.Defaults;
import twitch.gui.GUIColors;
import twitch.gui.Globals;
import twitch.gui.MutableAttributeSet;
import twitch.gui.ScrollManager;
import twitch.gui.ScrollWindow;
import twitch.gui.TwitchChatDocument;
import twitch.gui.MutableAttributeSet.ClickableListener;
import twitch.gui.graphics.EmoteWindow;
import twitch.gui.splash.StartupScreen;
import twitch.gui.splash.StartupScreenValues;
import twitch.listeners.CustomListeners;
import twitch.listeners.GlobalListeners;
import twitch.listeners.InternalListeners;
import twitch.listeners.types.ListenerType;
import twitch.message.ChatColor;
import twitch.message.MessageProcessor;
import twitch.message.ModerationTools;
import twitch.message.emotes.ChatEmote;
import twitch.message.types.LogMessage;
import twitch.message.types.Message;
import twitch.server.ServerList;
import twitch.user.User;
import twitch.user.UserList;
import twitch.user.UserListPopupMenu;
import twitch.user.badge.BitsImages;
import twitch.user.badge.GlobalBadges;
import twitch.utils.AdvancedString;
import twitch.utils.Dialogs;
import twitch.utils.Fonts;
import twitch.utils.Formats;
import twitch.utils.KeyPressed;
import twitch.utils.LoopingQueue;
import twitch.utils.MouseHandler;
import twitch.utils.Utils;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SplashScreen;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Component;
import java.awt.Cursor;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.border.LineBorder;
import javax.swing.JSeparator;
import javax.swing.JComboBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.ScrollPaneConstants;

@SuppressWarnings({ "rawtypes", "serial", "unchecked", "unused" })
public class Window extends JFrame implements ScrollWindow{
	
	public static Window window;
	
	private static final long serialVersionUID = 1L;
	private static JPanel contentPane;
	private static JTextField txtSendMessage;
	private static ChatConnection currentConnection;
	private static JTextPane chatLog;
	private static JScrollPane chatScrollPane;
	private static JScrollPane usersScrollPane;
	private static final JLabel lblStreamData = new JLabel("Disconnected");
	private static JList listUser;
	private static boolean showImages = true;
	private static JCheckBoxMenuItem chbxEnableImages;
	private static JComboBox boxUserListFilter;
	private static JList listConnections;
	private static JLabel lblStreamStatus;
	private static ConnectionPopupMenu connectionPopup;
	private static UserListPopupMenu userListPopup;
	private static JComboBox boxChannelSort;
	private JMenuItem miExit;
	//private static FollowedChannels followedChannels = new FollowedChannels();
	
	/**
	 * Launch the application.
	 */
	public static void launchWindow() {
		try {
			Window frame = new Window();
			frame.setVisible(true);
			frame.toFront();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create the frame.
	 */
	public Window() {
		Log.info("Creating main window...");
		StartupScreen.updateProgressValue(StartupScreenValues.WINDOW_MIN);
		window = this;
		
		setForeground(Color.DARK_GRAY);
		setTitle(Globals.NAME);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1128, 681);
		contentPane = new JPanel();
		contentPane.setBackground(GUIColors.BACKGROUND_COLOR);
		contentPane.setForeground(GUIColors.BACKGROUND_COLOR);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		try {
		     ClassLoader cl = this.getClass().getClassLoader();
		     ImageIcon programIcon = new ImageIcon(cl.getResource(Globals.APP_ICON));
		     setIconImage(programIcon.getImage());
		  } catch (Exception whoJackedMyIcon) {
		     System.out.println("Could not load program icon.");
		  }
		new Thread(new Runnable(){
			@Override
			public void run() {
				while(true){
					System.gc();
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {}
				}
			}
		}, "GCThread");
		
		chatLog = new JTextPane();
		connectionPopup = new ConnectionPopupMenu();
		userListPopup = new UserListPopupMenu();
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(Color.GRAY);
		menuBar.setForeground(Color.WHITE);
		setJMenuBar(menuBar);
		
		JMenu mnLogin = new JMenu("File");
		menuBar.add(mnLogin);
		
		JMenuItem miChangeLogin = new JMenuItem("Change Login");
		miChangeLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String user = Globals.USERNAME;
				String tok = Globals.TOKEN;
				new TokenRequestWindow();
				//User cancelled
				if(Globals.USERNAME == null || Globals.USERNAME.length() == 0 || Globals.TOKEN == null || Globals.TOKEN.length() == 0){
					Globals.USERNAME = user;
					Globals.TOKEN = tok;
				}
				else{
					Config.saveAll();
					try {
						Utils.restart();
					} catch (Exception e) {
					}
				}
			}
		});
		mnLogin.add(miChangeLogin);
		
		JMenuItem miLogout = new JMenuItem("Logout");
		miLogout.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				Database data = (Database) Config.getConfig(ConfigName.LOGIN);
				data.put("username", "");
				data.put("token", "");
				Config.saveAll();
				System.exit(0);
			}
		});
		mnLogin.add(miLogout);
		
		JSeparator separator_1 = new JSeparator();
		mnLogin.add(separator_1);
		
		miExit = new JMenuItem("Exit");
		mnLogin.add(miExit);
		
		JMenu mnNewMenu = new JMenu("Channel");
		menuBar.add(mnNewMenu);
		
		JMenuItem miJoinChannel = new JMenuItem("Join Channel");
		miJoinChannel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Connection.openJoinChannel();
			}
		});
		
		JMenuItem miAddChannel = new JMenuItem("Add Channel");
		miAddChannel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				Connection.openAddChannel();
			}
		});
		mnNewMenu.add(miAddChannel);
		
		JMenuItem miFollowedChannels = new JMenuItem("Followed Channels");
		miFollowedChannels.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				new FollowedChannelsWindow();
			}
		});
		mnNewMenu.add(miFollowedChannels);
		mnNewMenu.add(miJoinChannel);
		
		JMenuItem miLeaveChannel = new JMenuItem("Leave Channel");
		miLeaveChannel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!Dialogs.checkConnected()){
					return;
				}
				Connection.disconnect(currentConnection);
				currentConnection = null;
			}
		});
		mnNewMenu.add(miLeaveChannel);
		
		JMenuItem miChannelInfo = new JMenuItem("Channel Info");
		miChannelInfo.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(currentConnection == null || !currentConnection.isConnected()){
					Dialogs.error("Error", "You're currently not connected to a channel.");
					return;
				}
				UserModerationWindow.createUserWindow(UserList.getUser(currentConnection, currentConnection.getChannelName().substring(1).toLowerCase()));
			}
		});
		mnNewMenu.add(miChannelInfo);
		
		JSeparator separator = new JSeparator();
		mnNewMenu.add(separator);
		
		JMenu mnSubMode = new JMenu("Sub Mode");
		mnNewMenu.add(mnSubMode);
		
		JMenuItem miEnableSubMode = new JMenuItem("Enable");
		miEnableSubMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				ModerationTools.subMode(true);
			}
		});
		mnSubMode.add(miEnableSubMode);
		
		JMenuItem miDisableSubMode = new JMenuItem("Disable");
		miDisableSubMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ModerationTools.subMode(false);
			}
		});
		mnSubMode.add(miDisableSubMode);
		
		JMenu mnFollowerMode = new JMenu("Follower Mode");
		mnNewMenu.add(mnFollowerMode);
		
		JMenuItem miEnableFollowerMode = new JMenuItem("Enable");
		miEnableFollowerMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!Dialogs.checkConnected()){
					return;
				}
				String minutesS = Dialogs.input("Time", "Enter length (Minutes):");
				try{
					int parsed = Integer.parseInt(minutesS);
					ModerationTools.followerMode(true, parsed);
				} catch(Exception e1){
					Dialogs.error("Error", "Error: Input must be a number.");
				}
			}
		});
		mnFollowerMode.add(miEnableFollowerMode);
		
		JMenuItem miDisableFollowerMode = new JMenuItem("Disable");
		miDisableFollowerMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ModerationTools.followerMode(false, -1);
			}
		});
		mnFollowerMode.add(miDisableFollowerMode);
		
		JMenu mnSlowMode = new JMenu("Slow Mode");
		mnNewMenu.add(mnSlowMode);
		
		JMenuItem miEnableSlowMode = new JMenuItem("Enable");
		miEnableSlowMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!Dialogs.checkConnected()){
					return;
				}
				String secondS = Dialogs.input("Time", "Enter length (Seconds):");
				try{
					int parsed = Integer.parseInt(secondS);
					ModerationTools.slowMode(true, parsed);
				} catch(Exception e1){
					Dialogs.error("Error", "Error: Input must be a number.");
				}
			}
		});
		mnSlowMode.add(miEnableSlowMode);
		
		JMenuItem miDisableSlowMode = new JMenuItem("Disable");
		miDisableSlowMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ModerationTools.slowMode(false, -1);
			}
		});
		mnSlowMode.add(miDisableSlowMode);
		
		JMenu mnRkMode = new JMenu("R9K Mode");
		mnNewMenu.add(mnRkMode);
		
		JMenuItem miEnableR9KMode = new JMenuItem("Enable");
		miEnableR9KMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ModerationTools.r9KMode(true);
			}
		});
		mnRkMode.add(miEnableR9KMode);
		
		JMenuItem miDisableR9KMode = new JMenuItem("Disable");
		miDisableR9KMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ModerationTools.r9KMode(false);
			}
		});
		mnRkMode.add(miDisableR9KMode);
		
		JMenu mnEmoteonlyMode = new JMenu("Emote-Only Mode");
		mnNewMenu.add(mnEmoteonlyMode);
		
		JMenuItem miEnableEmoteOnlyMode = new JMenuItem("Enable");
		miEnableEmoteOnlyMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ModerationTools.emoteMode(true);
			}
		});
		mnEmoteonlyMode.add(miEnableEmoteOnlyMode);
		
		JMenuItem miDisableEmoteOnlyMode = new JMenuItem("Disable");
		miDisableEmoteOnlyMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ModerationTools.emoteMode(false);
			}
		});
		mnEmoteonlyMode.add(miDisableEmoteOnlyMode);
		
		JSeparator separator_6 = new JSeparator();
		mnNewMenu.add(separator_6);
		
		JMenuItem miHost = new JMenuItem("Host");
		miHost.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if(!Dialogs.checkConnected()){
					return;
				}
				String user = Dialogs.input("Host", "Who would you like to host?");
				if(user == null || user.equals("")){
					return;
				}
				ModerationTools.host(user);
			}
		});
		mnNewMenu.add(miHost);
		
		JMenuItem miUnhost = new JMenuItem("Unhost");
		miUnhost.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ModerationTools.unhost();
			}
		});
		mnNewMenu.add(miUnhost);
		
		JSeparator separator_7 = new JSeparator();
		mnNewMenu.add(separator_7);
		
		JMenuItem miCommercial = new JMenuItem("Run Commercial");
		miCommercial.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!Dialogs.checkConnected()){
					return;
				}
				String val = Dialogs.input("Commercial", "How long of a commercial? (seconds)");
				if(val == null || val.equals("")){
					return;
				}
				try{
					int sec = Integer.parseInt(val);
					ModerationTools.commercial(sec);
				} catch (Exception e1){
					Dialogs.error("Error", "Value must be an integer");
				}
			}
		});
		mnNewMenu.add(miCommercial);
		
		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);
		
		JMenuItem miGetUserHistory = new JMenuItem("User History");
		miGetUserHistory.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!Dialogs.checkConnected()){
					return;
				}
				String userS = JOptionPane.showInputDialog("Enter the username:");
				if(userS==null || userS.equals("")){
				}
				if(userS.equalsIgnoreCase("logger")){
					UserModerationWindow.createUserWindow(currentConnection.getLogger());
				}
				else{
					User user = UserList.getUser(currentConnection, userS.toLowerCase());
					UserModerationWindow.createUserWindow(user);
				}
			}
		});
		
		JMenuItem miSetColor = new JMenuItem("Set Color");
		miSetColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if(!Dialogs.checkConnected()){
					return;
				}
				Color newColor = JColorChooser.showDialog(null, "Choose Chat Color", Color.BLACK);
				if(newColor != null){
					ModerationTools.color(newColor);
				}
			}
		});
		
		JMenuItem miSearch = new JMenuItem("Search");
		miSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				SearchWindow window = new SearchWindow();
				window.setVisible(true);
			}
		});
		mnTools.add(miSearch);
		mnTools.add(miSetColor);
		mnTools.add(miGetUserHistory);
		
		JMenuItem miExportChat = new JMenuItem("Export Chat");
		miExportChat.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				new Thread(new Runnable(){
					@Override
					public void run() {
						String fileName = Dialogs.saveFile("Choose save location", currentConnection.getUsername() + "_"  + Formats.logDateFormatter.format(System.currentTimeMillis()) + ".log");
						if(fileName == null){
							return;
						}
						FileManager file = new FileManager(fileName);
						file.clearLines();
						for (String message : currentConnection.getChatLog()) {
							file.addLine(message);
						}
						file.save();
						file.close();
					}
				}, "ChatExporterThread").start();
			}
		});
		mnTools.add(miExportChat);
		
		final String enable = "Enable Overlays", disable = "Disable Overlays";
		
		JMenu mnFun = new JMenu("Fun");
		menuBar.add(mnFun);
		
		JMenuItem miSpamMessage = new JMenuItem("Spam Message");
		miSpamMessage.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!Dialogs.checkConnected()){
					return;
				}
				SpamWindow.openSpamWindow();
			}
		});
		mnFun.add(miSpamMessage);
		
		JMenuItem miEmoteView = new JMenuItem("Emote View");
		miEmoteView.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				ChatEmote.emoteWindow = EmoteWindow.newEmoteWindow(800, 600);
			}
		});
		mnFun.add(miEmoteView);
		
		final JCheckBoxMenuItem ckbxColor = new JCheckBoxMenuItem("Random Color");
		ckbxColor.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if(ckbxColor.isSelected()){
					ChatColor.enable();
				}
				else{
					ChatColor.disable();
				}
			}
		});
		mnFun.add(ckbxColor);
		
		JSeparator separator_5 = new JSeparator();
		mnFun.add(separator_5);
		
		JMenuItem miEnableTriggers = new JMenuItem("Enable Triggers");
		miEnableTriggers.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				GlobalListeners.enableListeners(true);
				currentConnection.getStyledChat().appendText("Triggers have been enabled.",  AttributeList.getLogAttribute(), true);
			}
		});
		mnFun.add(miEnableTriggers);
		
		JMenuItem miDisableTriggers = new JMenuItem("Disable Triggers");
		miDisableTriggers.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				GlobalListeners.enableListeners(false);
				currentConnection.getStyledChat().appendText("Triggers have been disabled.",  AttributeList.getLogAttribute(), true);
			}
		});
		mnFun.add(miDisableTriggers);
		
		JMenu mnOverlays = new JMenu("Overlays");
		menuBar.add(mnOverlays);
		final JMenuItem miToggleOverlays = new JMenuItem(enable);
		miToggleOverlays.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if(miToggleOverlays.getText().equals(enable)){
					ServerList.startAll();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
					miToggleOverlays.setText(disable);
				}
				else if(miToggleOverlays.getText().equals(disable)){
					ServerList.stopAll();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
					miToggleOverlays.setText(enable);
				}
			}
		});
		mnOverlays.add(miToggleOverlays);
		
		JSeparator separator_8 = new JSeparator();
		mnOverlays.add(separator_8);
		
		JMenuItem miAlertBox = new JMenuItem("Alert Box");
		miAlertBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
			}
		});
		mnOverlays.add(miAlertBox);
		
		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		JMenuItem miClearChat = new JMenuItem("Clear Chat Locally");
		miClearChat.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				chatLog.setText("");
			}
		});
		mnOptions.add(miClearChat);
		
		chbxEnableImages = new JCheckBoxMenuItem("Enable Images");
		chbxEnableImages.setSelected(true);
		chbxEnableImages.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				showImages = chbxEnableImages.isSelected();
			}
		});
		mnOptions.add(chbxEnableImages);
		
		final JCheckBoxMenuItem chbxEnableChat = new JCheckBoxMenuItem("Enable Chat");
		chbxEnableChat.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if(currentConnection == null){
					return;
				}
				MutableAttributeSet att = AttributeList.getLogAttribute();
				att.setUnderlined(false);
				if(chbxEnableChat.isSelected()){
					MessageProcessor.enableProcess(chbxEnableChat.isSelected());
					att.setColor(AttributeList.getJoinChannelAttribute().getColor());
					currentConnection.getMessageProcessor().process(new LogMessage(currentConnection, System.currentTimeMillis(), "Chat has been enabled...", att));
				}
				else{
					att.setColor(AttributeList.getLeaveChannelAttribute().getColor());
					currentConnection.getMessageProcessor().process(new LogMessage(currentConnection, System.currentTimeMillis(), "Chat has been disabled...", att));
					MessageProcessor.enableProcess(chbxEnableChat.isSelected());
				}
			}
		});
		chbxEnableChat.setSelected(true);
		mnOptions.add(chbxEnableChat);
		setContentPane(contentPane);
		/*Some piece of code*/
		addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	GlobalListeners.triggerListener(ListenerType.ON_SHUTDOWN, null);
	        	Defaults.saveDefaults();
	        	Config.saveAll();
		        ConnectionList.clearChannels();
		        System.exit(0);
		    }
		});
		
		chatScrollPane = new JScrollPane();
		chatScrollPane. setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//chatScrollPane. setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		usersScrollPane = new JScrollPane();
		usersScrollPane. setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		usersScrollPane. setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		
		lblStreamData.setForeground(Color.RED);
		lblStreamData.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblStreamData.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txtSendMessage = new JTextField();
		txtSendMessage.setForeground(Color.WHITE);
		txtSendMessage.setBackground(Color.BLACK);
		txtSendMessage.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		txtSendMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(currentConnection==null){
					return;
				}
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					currentConnection.sendMessage(txtSendMessage.getText());
					txtSendMessage.setText("");
				}
				else if(e.getKeyCode()==KeyEvent.VK_UP){
					txtSendMessage.setText(currentConnection.getMessageHistory().getReverse());
				}
				else if(e.getKeyCode()==KeyEvent.VK_DOWN){
					txtSendMessage.setText(currentConnection.getMessageHistory().get());
				}
			}
		});
		txtSendMessage.setColumns(10);
		
		JButton btnSendMessage = new JButton("Send");
		btnSendMessage.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		btnSendMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(currentConnection==null){
					return;
				}
				currentConnection.sendMessage(txtSendMessage.getText());
				txtSendMessage.setText("");
			}
		});
		
		listUser = new JList();
		listUser.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				//Double Left Click
				if(arg0.getButton() == 1 && arg0.getClickCount()==2){
					User user = (User) listUser.getSelectedValue();
					if(user==null){
						user = UserList.getUser(currentConnection, listUser.getSelectedValue().toString().toLowerCase());
					}
					UserModerationWindow.createUserWindow(user);
				}
				//Single Right Click
				if(arg0.getButton() == 3 && arg0.getClickCount() == 1){
					int index = listUser.locationToIndex(arg0.getPoint());
					listUser.setSelectedIndex(index);
					userListPopup.show(arg0.getComponent(), arg0.getX(), arg0.getY());
				}
			}
		});
		listUser.setCellRenderer(new DefaultListCellRenderer(){
			@Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof User){
                	User user = (User) value;
                	setText(user.getBadgeName());
                	GlobalBadges badge = user.getHighestBadge();
                	if(badge != null){
                		setForeground(badge.getColor());
                	}
                	else{
                		setForeground(Color.WHITE);
                	}
                }
				return c;
			}
		});
		usersScrollPane.setViewportView(listUser);
		listUser.setForeground(Color.WHITE);
		listUser.setBackground(Color.BLACK);
		listUser.setBorder(new LineBorder(new Color(0, 0, 0)));
		listUser.setModel(new AbstractListModel() {
			String[] values = new String[] {};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		
		String[] list = new String[GlobalBadges.values().length+2];
		list[0] = "EVERYONE";
		int counter = 1;
		for(GlobalBadges type : GlobalBadges.values()){
			list[counter++] = type.getLongName();
		}
		list[list.length-1] = "NONE";
		
		boxUserListFilter = new JComboBox(list);
		boxUserListFilter.setForeground(Color.WHITE);
		boxUserListFilter.setBackground(Color.BLACK);
		boxUserListFilter.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					updateUserList();
				}
			}
		});
		
		JScrollPane connectionScrollPane = new JScrollPane();
		connectionScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		connectionScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		lblStreamStatus = new JLabel("Title");
		lblStreamStatus.setForeground(Color.RED);
		lblStreamStatus.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		ChannelSortType[] sortList = ChannelSortType.values();
		boxChannelSort = new JComboBox(sortList);
		boxChannelSort.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if(((ChannelSortType)arg0.getItem()).equals((ChannelSortType)boxChannelSort.getSelectedItem())){
					ChannelSortType type = (ChannelSortType) arg0.getItem();
					Window.sortChannelList(type);
				}
			}
		});
		boxChannelSort.setBackground(Color.BLACK);
		boxChannelSort.setForeground(Color.WHITE);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addComponent(connectionScrollPane, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
								.addComponent(boxChannelSort, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(chatScrollPane, GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
										.addComponent(boxUserListFilter, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
										.addComponent(usersScrollPane, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(txtSendMessage, GroupLayout.DEFAULT_SIZE, 879, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnSendMessage))))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblStreamStatus, GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblStreamData, GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)))
					.addGap(1))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblStreamData)
						.addComponent(lblStreamStatus))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(boxUserListFilter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(usersScrollPane, GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE))
								.addComponent(chatScrollPane, GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE))
							.addGap(9)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(txtSendMessage, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnSendMessage, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(boxChannelSort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(connectionScrollPane, GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)))
					.addContainerGap())
		);
		
		listConnections = new JList();
		listConnections.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				//Double Left Click
				if(arg0.getButton() == 1 && arg0.getClickCount() == 2){
					ChatConnection connection = (ChatConnection) listConnections.getSelectedValue();
					if(!connection.isConnected()){
						connection.connect();
					}
					changeChannels(connection.getChannelName());
				}
				//Single Right Click
				if(arg0.getButton() == 3 && arg0.getClickCount() == 1){
					int index = listConnections.locationToIndex(arg0.getPoint());
					listConnections.setSelectedIndex(index);
					connectionPopup.show(arg0.getComponent(), arg0.getX(), arg0.getY());
				}
			}
		});
		
		listConnections.setCellRenderer(new DefaultListCellRenderer(){
			@Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof ChatConnection){
                	ChatConnection tempConnect = (ChatConnection) value;
                	setText(tempConnect.getChannelName());
                	setFont(Fonts.notLiveFont);
                	if(tempConnect.isConnected()){
                		setForeground(Color.GREEN);
                	}
                	else{
                		setForeground(Color.RED);
                	}
                  	if(StreamListener.getStreamData(tempConnect) != null && StreamListener.getStreamData(tempConnect).isLive()){
                		setFont(Fonts.liveFont);
                	}
                }
				return c;
			}
		});
		listConnections.setForeground(Color.WHITE);
		listConnections.setBackground(Color.BLACK);
		connectionScrollPane.setViewportView(listConnections);
		chatLog.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				MouseHandler.executeClick(chatLog, e);
			}
		});
		chatLog.addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseMoved(MouseEvent e){
				MouseHandler.updateMouse(window, chatLog, e);
			}
		});
		chatScrollPane.setViewportView(chatLog);
		chatLog.setEditable(false);
		chatLog.setBackground(Color.BLACK);
		contentPane.setLayout(gl_contentPane);
		
		Log.info("Finished creating main window");
		StartupScreen.updateProgressValue(StartupScreenValues.WINDOW_MAX);
		
		ServerList.loadAllServers();
		listConnections.setListData(ConnectionList.getChannelArray());
		Defaults.loadDefaults();
		new StreamListener();
		StartupScreen.updateProgressValue(StartupScreenValues.FINISHED);
		
		GlobalListeners.triggerListener(ListenerType.ON_STARTUP, null);
		
		if(currentConnection == null || !currentConnection.isConnected()){
			setDisconnected();
		}
		
		Log.info("Everything loaded");
		closeSplashScreen();
	}
	
	public static void clearTextArea(){
		chatLog.setText("");
	}
	
	public static void changeChannels(String channel){
		listConnections.setListData(ConnectionList.getChannelArray());
		if(channel == null){
			currentConnection = null;
			return;
		}
		currentConnection = ConnectionList.getChannel(channel);
		if(!ConnectionList.getChannel(channel).isConnected()){
			return;
		}
		//Chat Window
		chatLog.setDocument(currentConnection.getStyledChat());
		chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		chatScrollPane.setViewportView(chatLog);
		//User List & Label
		new Thread(new Runnable(){
			@Override
			public void run() {
				Window.setUserList(UserList.getUserData(currentConnection).values());
				currentConnection.scroll();
			}
		}, "UserListChangeThread").start();
	}
	
	public static void sortChannelList(ChannelSortType type){
		Vector<ChatConnection> connectionList = null;
		if(type == ChannelSortType.ALL){
			connectionList = new Vector<ChatConnection>(Arrays.asList(ConnectionList.getChannelArray()));
		}
		else if(type == ChannelSortType.CONNECTED || type == ChannelSortType.DISCONNECTED){
			connectionList = new Vector<ChatConnection>();
			for(ChatConnection connection : ConnectionList.getConnections()){
				if((type == ChannelSortType.CONNECTED && connection.isConnected()) || (type == ChannelSortType.DISCONNECTED && !connection.isConnected())){
					connectionList.add(connection);
				}
			}
		}
		else if(type == ChannelSortType.LIVE || type == ChannelSortType.NOT_LIVE){
			connectionList = new Vector<ChatConnection>();
			for(ChatConnection connection : ConnectionList.getConnections()){
				if((type == ChannelSortType.LIVE && StreamListener.getStreamData(connection).isLive()) || (type == ChannelSortType.NOT_LIVE && !StreamListener.getStreamData(connection).isLive())){
					connectionList.add(connection);
				}
			}
		}
		else{
			return;
		}
		refreshChannelList(connectionList);
	}
	
	public static void refreshChannelList(){
		sortChannelList((ChannelSortType)boxChannelSort.getSelectedItem());
	}
	
	public static void refreshChannelList(Vector<ChatConnection> connection){
		if(connection == null){
			return;
		}
		Collections.sort(connection);
		int selectedIndex = listConnections.getSelectedIndex();
		listConnections.clearSelection();
		listConnections.setListData(connection);
		listConnections.setSelectedIndex(selectedIndex);
	}
	
	public static void updateUserList() {
		if (currentConnection == null) {
			return;
		}
		synchronized (UserList.getLock(currentConnection)) {
			String item = getFilterType();
			if (item.equals("EVERYONE")) {
				Window.setUserList(UserList.getUserData(currentConnection).values());
			} else {
				HashSet<User> list = new HashSet<User>();
				if (item.equals("NONE")) {
					for (User user : UserList.getUserData(currentConnection).values()) {
						if (!user.hasType()) {
							list.add(user);
						}
					}
				} else {
					GlobalBadges type = GlobalBadges.valueOf(item);
					for (User user : UserList.getUserData(currentConnection).values()) {
						if (user.isType(type)) {
							list.add(user);
						}
					}
				}
				Window.setUserList(list);
				list.clear();
				list = null;
			}
		}
	}
	
	public static void setUserList(Collection<User> list){
		if(currentConnection==null || !UserList.hasConnection(currentConnection)){
			return;
		}
		Vector<User> usernameList = new Vector<User>(list);
		Collections.sort(usernameList);
		int notConnectedCount = 0;
		for(int userCount = 0; userCount < usernameList.size(); userCount++){
			if(usernameList.get(userCount).isConnected()){
				usernameList.set(userCount - notConnectedCount, usernameList.get(userCount));
			}
			else{
				notConnectedCount++;
			}
		}
		setConnectedText();
		listUser.setListData(usernameList);
	}
	
	private static void setStreamData(String text, Color color){
		Window.lblStreamData.setText(text);
		Window.lblStreamData.setForeground(color);
	}
	
	public static JList getUserList(){
		return listUser;
	}
	
	public static String getFilterType(){
		return boxUserListFilter.getSelectedItem().toString();
	}
	
	public static void setChannelConnection(ChatConnection connect){
		currentConnection = connect;
		if(currentConnection == null){
			listConnections.setListData(ConnectionList.getChannelArray());
			//Chat Window
			chatLog.setDocument(new DefaultStyledDocument());
			chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			chatScrollPane.setViewportView(chatLog);
			//User List & Label
			new Thread(new Runnable(){
				@Override
				public void run() {
					listUser.setListData(new Vector<String>());
				}
			}, "UserListChangeThread").start();
		}
	}
	
	public static ChatConnection getCurrentConnection(){
		return currentConnection;
	}
	
	public static TwitchChatDocument getStyledChat(){
		return currentConnection.getStyledChat();
	}
	
	public static boolean isConnected(){
		return currentConnection != null && currentConnection.isConnected();
	}
	
	public static boolean showImages(){
		return showImages;
	}
	
	public static void setShowImages(boolean val){
		showImages = val;
		chbxEnableImages.setSelected(val);
	}
	
	public static void setConnectedText(){
		if(currentConnection == null){
			setDisconnected();
			return;
		}
		String message = currentConnection.getChannelName();
		ChatConnection connection = currentConnection;
		Color color = new Color(13, 168, 16);
		if(StreamListener.getStreamData(connection) == null || !UserList.hasConnection(currentConnection)){
			setStreamData(message, color);
			return;
		}
		if(!currentConnection.isConnected()){
			setDisconnected();
			return;
		}
		if(StreamListener.getStreamData(connection).isLive()){
			message += " (Live) - ";
			message += "Chatters: " + Formats.numberCommaFormatter.format(UserList.getUserCount(currentConnection));
			message += " - Viewers: " + Formats.numberCommaFormatter.format(StreamListener.getStreamData(connection).getViewers());
		}
		else{
			message += " (Offline) - ";
			color = Color.RED;
			message += "Chatters: " + UserList.getUserCount(currentConnection);
			
		}
		String title;
		if(StreamListener.getStreamData(connection).getStatus() != null){
			title = StreamListener.getStreamData(connection).getStatus();
		}
		else{
			title = connection.getChannelInfo().getStatus();
		}
		lblStreamStatus.setText(AdvancedString.ellipsize(lblStreamStatus.getFont(), title, Window.window.getWidth() - AdvancedString.getFontLength(lblStreamData.getFont(), lblStreamData.getText())));
		lblStreamStatus.setForeground(color);
		setStreamData(message, color);	
	}
	
	public static void setDisconnected(){
		lblStreamStatus.setText("");
		setStreamData("Disconnected", Color.RED);
	}
	
	public static ChatConnection getSelectedChannel(){
		return (ChatConnection) listConnections.getSelectedValue();
	}
	
	public static User getSelectedUser(){
		return (User) listUser.getSelectedValue();
	}

	public static void moveToFront(){
		window.toFront();
	}
	
	public static void closeSplashScreen(){
		StartupScreen.stop();
		Window.moveToFront();
	}

	@Override
	public void updateScrollBar(ChatConnection connect) {
		if(connect != currentConnection){
			return;
		}
		ScrollManager.scrollDown(chatScrollPane, chatLog);
	}
}