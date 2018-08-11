package twitch.gui.windows;

import java.awt.Font;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import twitch.channels.ChannelInfo;
import twitch.gui.GUIColors;
import twitch.message.*;
import twitch.message.types.Message;
import twitch.user.User;
import twitch.utils.Formats;
import twitch.utils.TimeConversion;

import javax.swing.JScrollPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;

import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.awt.event.ActionEvent;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Rectangle;

public class UserModerationWindow extends JFrame {

	private static final int IMAGE_DIM = 128;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private User user;
	private ChannelInfo channelInfo;
	private Message currentMessage = null;
	private JTextArea txtChatHistory;
	private JLabel lblNumbers;
	private JLabel lblCreated;
	private JLabel lblLogo;
	private JLabel lblName;
	private JLabel lblGame;
	private JLabel lblStatus;
	private Thread chatUpdateThread;
	private boolean shouldStop = false;
	
	public static void createUserWindow(final User user){
		if(user == null){
			return;
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UserModerationWindow userWindow = new UserModerationWindow(user);
					userWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public UserModerationWindow(final User user){
		this.user = user;
		this.channelInfo = user.getChannelInfo();
		setBackground(Color.DARK_GRAY);
		setResizable(false);
		setBounds(100, 100, 566, 654);
		contentPane = new JPanel();
		contentPane.setBackground(GUIColors.BACKGROUND_COLOR);
		contentPane.setForeground(GUIColors.BACKGROUND_COLOR);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		
		lblName = new JLabel("user");
		lblName.setText(user.getBadgeName());
		lblName.setForeground(Color.WHITE);
		lblName.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		JButton btn10s = new JButton("10s");
		btn10s.setFont(new Font("Dialog", Font.PLAIN, 10));
		btn10s.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ModerationTools.timeout(user, 10);
			}
		});
		
		JButton btn1m = new JButton("1m");
		btn1m.setFont(new Font("Dialog", Font.PLAIN, 10));
		btn1m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ModerationTools.timeout(user, TimeConversion.ONE_MIN.getSeconds());
			}
		});
		
		JButton btn5m = new JButton("5m");
		btn5m.setFont(new Font("Dialog", Font.PLAIN, 10));
		btn5m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ModerationTools.timeout(user, TimeConversion.FIVE_MIN.getSeconds());
			}
		});
		
		JButton btn10m = new JButton("10m");
		btn10m.setFont(new Font("Dialog", Font.PLAIN, 10));
		btn10m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ModerationTools.timeout(user, TimeConversion.TEN_MIN.getSeconds());
			}
		});
		
		JButton btn1h = new JButton("1h");
		btn1h.setFont(new Font("Dialog", Font.PLAIN, 10));
		btn1h.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ModerationTools.timeout(user, TimeConversion.ONE_HOUR.getSeconds());
			}
		});
		
		JButton btn12h = new JButton("12h");
		btn12h.setFont(new Font("Dialog", Font.PLAIN, 10));
		btn12h.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ModerationTools.timeout(user, TimeConversion.TWELVE_HOUR.getSeconds());
			}
		});
		
		JButton btn1d = new JButton("1d");
		btn1d.setFont(new Font("Dialog", Font.PLAIN, 10));
		btn1d.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ModerationTools.timeout(user, TimeConversion.ONE_HOUR.getSeconds());
			}
		});
		
		JButton btn1w = new JButton("1w");
		btn1w.setFont(new Font("Dialog", Font.PLAIN, 10));
		btn1w.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ModerationTools.timeout(user, TimeConversion.ONE_WEEK.getSeconds());
			}
		});
		
		JButton btnPurge = new JButton("Purge");
		btnPurge.setFont(new Font("Dialog", Font.PLAIN, 10));
		btnPurge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ModerationTools.purge(user);
			}
		});
		
		JButton btnBan = new JButton("Ban");
		btnBan.setFont(new Font("Dialog", Font.PLAIN, 10));
		btnBan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ModerationTools.ban(user);
			}
		});
		
		JButton btnUnban = new JButton("Unban");
		btnUnban.setFont(new Font("Dialog", Font.PLAIN, 10));
		btnUnban.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ModerationTools.unban(user);
			}
		});
		
		JButton btnCustom = new JButton("Other");
		btnCustom.setFont(new Font("Dialog", Font.PLAIN, 10));
		
		JSeparator separator = new JSeparator();
		
		JButton btnGoToPage = new JButton("Open Twitch Page");
		btnGoToPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("http://twitch.tv/" + user.getUsername() + "/"));
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnGoToPage.setFont(new Font("Dialog", Font.PLAIN, 10));
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		
		JButton btnMod = new JButton("Mod");
		btnMod.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ModerationTools.mod(user);
			}
		});
		btnMod.setFont(new Font("Dialog", Font.PLAIN, 10));
		
		JButton btnUnmod = new JButton("Unmod");
		btnUnmod.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ModerationTools.unmod(user);
			}
		});
		btnUnmod.setFont(new Font("Dialog", Font.PLAIN, 10));
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setOrientation(SwingConstants.VERTICAL);
		
		lblNumbers = new JLabel(" Followers, Views");
		lblNumbers.setForeground(Color.WHITE);
		
		lblCreated = new JLabel("Created: ");
		lblCreated.setForeground(Color.WHITE);
		
		lblLogo = new JLabel("");
		lblLogo.setIcon(new ImageIcon("G:\\Pictures\\white.png"));
		lblLogo.setIconTextGap(0);
		lblLogo.setVerticalAlignment(SwingConstants.TOP);
		lblLogo.setHorizontalAlignment(SwingConstants.RIGHT);
		lblLogo.setBounds(new Rectangle(0, 0, 96, 96));
		lblLogo.setAlignmentY(Component.TOP_ALIGNMENT);
		
		lblGame = new JLabel("Game: ");
		lblGame.setForeground(Color.WHITE);
		
		lblStatus = new JLabel("Status:");
		lblStatus.setForeground(Color.WHITE);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblStatus, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
						.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(btnPurge, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btn10s, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btn1m, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btn5m, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btn10m, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(btn1h, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btn12h, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btn1d, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btn1w, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnCustom, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(btnBan, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnUnban, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(separator_2, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addComponent(btnMod, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnUnmod, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)))
						.addComponent(separator, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
									.addGroup(gl_contentPane.createSequentialGroup()
										.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
											.addComponent(btnGoToPage, GroupLayout.PREFERRED_SIZE, 138, GroupLayout.PREFERRED_SIZE)
											.addComponent(lblGame, GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))
										.addPreferredGap(ComponentPlacement.RELATED, 10, GroupLayout.PREFERRED_SIZE))
									.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(lblNumbers)
										.addPreferredGap(ComponentPlacement.RELATED)))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblCreated)
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addComponent(lblLogo))
						.addComponent(lblName))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(6)
					.addComponent(lblName)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
							.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_contentPane.createSequentialGroup()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
									.addComponent(btnPurge, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(btn10s, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
										.addComponent(btn1m, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
										.addComponent(btn5m, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
										.addComponent(btn10m, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
									.addComponent(btn1h, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(btn12h, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
										.addComponent(btn1d, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
									.addComponent(btn1w, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
									.addComponent(btnCustom, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnBan, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnUnban, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
						.addComponent(separator_2, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnMod, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnUnmod, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnGoToPage, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblNumbers)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblCreated)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(lblGame))
						.addComponent(lblLogo))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblStatus)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		txtChatHistory = new JTextArea();
		scrollPane.setViewportView(txtChatHistory);
		txtChatHistory.setBackground(Color.BLACK);
		txtChatHistory.setForeground(Color.WHITE);
		txtChatHistory.setEditable(false);
		this.setTitle(user.getUsername() + " - User Info");
		contentPane.setLayout(gl_contentPane);
		chatUpdateThread = new Thread(new ChatThread(), "UserModerationChatUpdateThread");
		chatUpdateThread.start();
		this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
            	shouldStop = true;
            }
        });
		new Thread(){
			public void run(){
				updateData();
			}
		}.start();
	}
	
	public void resetChat(){
		if(user==null || user.getChatHistory().size()==0){
			return;
		}
		if(currentMessage==null){
			currentMessage = user.getChatHistory().getFirst();
		}
		else if(currentMessage.equals(user.getChatHistory().getFirst())){
			return;
		}
		txtChatHistory.setText("");
		currentMessage = user.getChatHistory().getFirst();
		for(int i = user.getChatHistory().size()-1; i >= 0; i--){
			txtChatHistory.append(user.getChatHistory().get(i) + "\n");
		}
	}
	
	private void updateData(){
		String label = Formats.numberCommaFormatter.format(channelInfo.getFollowers()) + " Followers, " +  Formats.numberCommaFormatter.format(channelInfo.getViews()) + " Views";
		if(channelInfo.isPartnered()){
			label += ", Partnered";
		}
		else{
			label += ", Unpartnered";
		}
		lblNumbers.setText(label);
		try {
			Date date =  Formats.origDateFormatter.parse(channelInfo.getCreated().replaceAll("Z$", "+0000"));
			label =  Formats.newDateFormatter.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		lblCreated.setText(label);
		lblGame.setText("Game: " + channelInfo.getGame());
		lblStatus.setText("Status: " + channelInfo.getStatus());
		if(channelInfo.getLogo()!=null){
			Image logo = channelInfo.getLogo().getScaledInstance(IMAGE_DIM, IMAGE_DIM, Image.SCALE_DEFAULT);
			lblLogo.setIcon(new ImageIcon(logo));	
		}
		if(channelInfo.getDisplayName()!=null){
			lblName.setText(user.getBadges() + channelInfo.getDisplayName());
			user.setDisplayName(channelInfo.getDisplayName());
		}
	}
	
	public class ChatThread implements Runnable{
		@Override
		public void run() {
			while(true){
				resetChat();
				if(shouldStop){
					break;
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
