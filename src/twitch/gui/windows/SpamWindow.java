package twitch.gui.windows;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import twitch.connection.ChatConnection;
import twitch.gui.GUIColors;
import twitch.utils.Dialogs;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SpamWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4668825502064226040L;
	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextField txtMessagePerMinute;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JTextField txtNumOfMessages;

	private Thread thread;
	private SpamThread spamThread;
	/**
	 * Launch the application.
	 */
	public static void openSpamWindow(){
		if(!Window.isConnected()){
			Dialogs.error("Error", "You must connect to a channel before\nthis window can be opened.");
			return;
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SpamWindow spamWindow = new SpamWindow();
					spamWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public SpamWindow() {
		setResizable(false);
		setTitle("Spam Message");
		setBackground(GUIColors.BACKGROUND_COLOR);
		setBounds(100, 100, 591, 154);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		txtMessage = new JTextField();
		txtMessage.setColumns(10);
		
		JLabel lblMessage = new JLabel("Message:");
		
		txtMessagePerMinute = new JTextField();
		txtMessagePerMinute.setColumns(10);
		
		lblNewLabel = new JLabel("Messages Per Minute:");
		
		lblNewLabel_1 = new JLabel("Number of Messages:");
		
		txtNumOfMessages = new JTextField();
		txtNumOfMessages.setColumns(10);
		
		JButton btnStart = new JButton("Start");
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				startSpam();
			}
		});
		
		JButton btnStop = new JButton("Stop");
		btnStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				stopSpam();
			}
		});
		
		JButton btnExit = new JButton("Exit");
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				stopSpam();
				SpamWindow.this.dispose();
				SpamWindow.this.setVisible(false);
			}
		});
		
		JButton btnInfo = new JButton("Info");
		btnInfo.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				Dialogs.information("Information", "Important Information:\n"
						+ "- You MUST be a moderator of the channel\n"
						+ "- This program increases the global limit to " + ChatConnection.INSTANCES*100*2 + " messages per minute as a moderator\n"
						+ "- You can spam more than that, but be careful!\n"
						+ "- I am not responsible for any banning that may occur");
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(lblNewLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblMessage, Alignment.LEADING))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(txtMessage, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(txtMessagePerMinute, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblNewLabel_1)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(txtNumOfMessages, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnStart, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnStop, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
							.addGap(115)
							.addComponent(btnInfo, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnExit, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtMessage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMessage))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(txtMessagePerMinute, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_1)
						.addComponent(txtNumOfMessages, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnStart)
						.addComponent(btnStop)
						.addComponent(btnExit)
						.addComponent(btnInfo))
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
	}
	
	private class SpamThread implements Runnable{

		private String message;
		private int amount, time;
		private boolean stop = false;
		
		public SpamThread(String message, int amount, int time){
			this.message = message;
			this.amount = amount;
			this.time = time;
		}
		
		private boolean addition = true;
		
		@Override
		public void run() {
			for(int i = 0; i < amount; i++){
				if(addition)
					Window.getCurrentConnection().sendMessage(message+" .");
				else
					Window.getCurrentConnection().sendMessage(message);
				addition = !addition;	
				try {
					Thread.sleep((long) (1000.0*(60.0/(double)time)));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(stop){
					return;
				}
			}
			/*int count = 0;
			for(User user : UserList.getUserData(Window.getCurrentConnection()).values()){
				if(!user.isMod() && count<90){
					ModerationTools.purge(user);
					count++;
					System.out.println(user.getUsername());
				}
				if(stop){
					return;
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}*/
		}
		
		public void stopSpam(){
			stop = true;
		}
	}
	
	private void startSpam(){
		int amount, time;
		try{
			amount = Integer.parseInt(txtNumOfMessages.getText());
		}catch(Exception e1){
			amount = 0;
		}
		try{
			time = Integer.parseInt(txtMessagePerMinute.getText());
		}catch(Exception e1){
			time = 0;
		}
		if(amount<=0){
			Dialogs.error("Error", "Number of Messages must be a positive integer.");
			return;
		}
		if(time<=0){
			Dialogs.error("Error", "Messages per Minute must be a positive integer.");
			return;
		}
		if(thread != null && thread.isAlive()){
			Dialogs.error("Error", "Message already spamming.");
			return;
		}
		spamThread = new SpamThread(txtMessage.getText(), amount, time);
		thread = new Thread(spamThread, "MessageSpamThread");
		thread.start();
	}
	
	private void stopSpam(){
		if(thread == null){
			return;
		}
		if(!thread.isAlive()){
			return;
		}
		spamThread.stopSpam();
		spamThread = null;
		thread = null;
	}
}
