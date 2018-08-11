package twitch.connection;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import twitch.channels.StreamListener;
import twitch.gui.windows.StreamInfoWindow;
import twitch.gui.windows.TwitchChatWindow;
import twitch.gui.windows.Window;
import twitch.utils.Dialogs;

/*
 * RIGHT CLICK MENU FOR CHANNEL CONFIGURATION
 */
public class ConnectionPopupMenu extends JPopupMenu{

	private static final long serialVersionUID = 1L;
	private JMenuItem add, join, openTwitchPage, viewStream, openWindow, streamInfo, connect, disconnect, disconnectAll, remove, removeAll;
	
	public ConnectionPopupMenu(){
		super();
		
		add = new JMenuItem("Add Channel");
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Connection.openAddChannel();
			}
		});
		add(add);
		
		join = new JMenuItem("Join Channel");
		join.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Connection.openJoinChannel();
			}
		});
		add(join);
		
		add(new JPopupMenu.Separator());
		
		remove = new JMenuItem("Remove Channel");
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(Window.getSelectedChannel().isConnected()){
					Window.getSelectedChannel().disconnect();
				}
				boolean change = false;
				if(Window.getSelectedChannel().equals(Window.getCurrentConnection())){
					change = true;
				}
				ConnectionList.removeChannel(Window.getSelectedChannel());
				String channel = ConnectionList.getFirstConnectedChannel();
				if(change){
					Window.changeChannels(channel);
				}
			}
		});
		add(remove);
		
		removeAll = new JMenuItem("Remove All Channels");
		removeAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for(ChatConnection connection : ConnectionList.getConnections()){
					if(connection.isConnected()){
						Window.getSelectedChannel().disconnect();
					}
					ConnectionList.removeChannel(Window.getSelectedChannel());
				}
				Window.setChannelConnection(null);
				Window.setDisconnected();
				
			}
		});
		add(removeAll);
		
		add(new JPopupMenu.Separator());
		
		
		connect = new JMenuItem("Connect");
		connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(Window.getSelectedChannel().isConnected()){
					Dialogs.information("Info", "Channel is already connected.");
				}
				else{
					Window.getSelectedChannel().connect();
				}
			}
		});
		add(connect);
		
		disconnect = new JMenuItem("Disconnect");
		disconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!Window.getSelectedChannel().isConnected()){
					Dialogs.information("Info", "Channel is currently not connected.");
				}
				else{
					Window.getSelectedChannel().disconnect();
					String channel = ConnectionList.getFirstConnectedChannel();
					if(Window.getSelectedChannel().equals(Window.getCurrentConnection())){
						Window.changeChannels(channel);
					}
				}
			}
		});
		add(disconnect);
		
		disconnectAll = new JMenuItem("Disconnect All");
		disconnectAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for(ChatConnection connection : ConnectionList.getConnections()){
					if(connection.isConnected()){
						connection.disconnect();
					}
				}
				Window.setChannelConnection(null);
				Window.setDisconnected();
			}
		});
		add(disconnectAll);
		
		add(new JPopupMenu.Separator());
		
		
		openWindow = new JMenuItem("Open Chat Window");
		openWindow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!Window.getSelectedChannel().isConnected()){
					Window.getSelectedChannel().connect();
				}
				TwitchChatWindow.launchTwitchChatWindow(Window.getSelectedChannel());
			}
		});
		add(openWindow);
		
		viewStream = new JMenuItem("Open Stream Window");
		viewStream.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				URLLoader.openUrl("https://player.twitch.tv/?channel=" + Window.getSelectedChannel().getChannelInfo().getChannel() + "&html5");
			}
		});
		add(viewStream);
		
		streamInfo = new JMenuItem("View Stream Details");
		streamInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				StreamInfoWindow.createStreamInfoWindow(StreamListener.getStreamData(Window.getSelectedChannel()));
			}
		});
		add(streamInfo);
		
		openTwitchPage = new JMenuItem("Open Twitch Page");
		openTwitchPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Desktop.getDesktop().browse(new URI("http://twitch.tv/" + Window.getSelectedChannel().getUsername() + "/"));
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		add(openTwitchPage);

	}
}
