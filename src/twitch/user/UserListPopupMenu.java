package twitch.user;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import twitch.gui.windows.UserModerationWindow;
import twitch.gui.windows.Window;
import twitch.message.ModerationTools;
import twitch.utils.TimeConversion;

/*
 * RIGHT CLICK MENU FOR USER LIST
 */
public class UserListPopupMenu extends JPopupMenu{

	private static final long serialVersionUID = 1L;
	private JMenuItem userInfo, twitchPage, purge, timeMin, timeTenMin, timeOneHour, timeTwelveHour, timeOneDay, timeOneWeek, ban, unban, mod, unmod;
	
	public UserListPopupMenu(){
		super();
		
		userInfo = new JMenuItem("User Info");
		userInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				User user = Window.getSelectedUser();
				if(user == null){
					return;
				}
				UserModerationWindow.createUserWindow(user);
			}
		});
		add(userInfo);
		
		twitchPage = new JMenuItem("Open Twitch Page");
		twitchPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				User user = Window.getSelectedUser();
				if(user == null){
					return;
				}
				try {
					Desktop.getDesktop().browse(new URI("http://twitch.tv/" + user.getUsername() + "/"));
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		add(twitchPage);
		
		add(new JPopupMenu.Separator());
		
		purge = new JMenuItem("Purge");
		purge.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				User user = Window.getSelectedUser();
				if(user == null){
					return;
				}
				ModerationTools.purge(user);
			}
		});
		add(purge);
		
		timeMin = new JMenuItem("1 Min");
		timeMin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				User user = Window.getSelectedUser();
				if(user == null){
					return;
				}
				ModerationTools.timeout(user, TimeConversion.ONE_MIN.getSeconds());
			}
		});
		add(timeMin);
		
		timeTenMin = new JMenuItem("10 Min");
		timeTenMin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				User user = Window.getSelectedUser();
				if(user == null){
					return;
				}
				ModerationTools.timeout(user, TimeConversion.TEN_MIN.getSeconds());
			}
		});
		add(timeTenMin);
		
		timeOneHour = new JMenuItem("1 Hour");
		timeOneHour.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				User user = Window.getSelectedUser();
				if(user == null){
					return;
				}
				ModerationTools.timeout(user, TimeConversion.ONE_HOUR.getSeconds());
			}
		});
		add(timeOneHour);
		
		timeTwelveHour = new JMenuItem("12 Hour");
		timeTwelveHour.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				User user = Window.getSelectedUser();
				if(user == null){
					return;
				}
				ModerationTools.timeout(user, TimeConversion.TWELVE_HOUR.getSeconds());
			}
		});
		add(timeTwelveHour);
		
		timeOneDay = new JMenuItem("1 Day");
		timeOneDay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				User user = Window.getSelectedUser();
				if(user == null){
					return;
				}
				ModerationTools.timeout(user, TimeConversion.ONE_DAY.getSeconds());
			}
		});
		add(timeOneDay);
		
		
		timeOneWeek = new JMenuItem("1 Week");
		timeOneWeek.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				User user = Window.getSelectedUser();
				if(user == null){
					return;
				}
				ModerationTools.timeout(user, TimeConversion.ONE_WEEK.getSeconds());
			}
		});
		add(timeOneWeek);
		
		ban = new JMenuItem("Ban");
		ban.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				User user = Window.getSelectedUser();
				if(user == null){
					return;
				}
				ModerationTools.ban(user);
			}
		});
		add(ban);
		
		unban = new JMenuItem("Unban");
		unban.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				User user = Window.getSelectedUser();
				if(user == null){
					return;
				}
				ModerationTools.unban(user);
			}
		});
		add(unban);
		
		add(new JPopupMenu.Separator());
		
		mod = new JMenuItem("Mod");
		mod.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				User user = Window.getSelectedUser();
				if(user == null){
					return;
				}
				ModerationTools.mod(user);
			}
		});
		add(mod);
		
		unmod = new JMenuItem("Unmod");
		unmod.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				User user = Window.getSelectedUser();
				if(user == null){
					return;
				}
				ModerationTools.unmod(user);
			}
		});
		add(unmod);
	}
}
