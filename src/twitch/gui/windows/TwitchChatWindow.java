package twitch.gui.windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import twitch.connection.ChatConnection;
import twitch.gui.ScrollManager;
import twitch.gui.ScrollWindow;
import twitch.utils.MouseHandler;

import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class TwitchChatWindow extends JFrame implements ScrollWindow{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JTextPane txtChat;
	private final TwitchChatWindow window = this;
	private JTextField txtSend;
	
	/**
	 * Launch the application.
	 */
	public static void launchTwitchChatWindow(final ChatConnection connect) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TwitchChatWindow frame = new TwitchChatWindow(connect);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TwitchChatWindow(final ChatConnection connect) {
		setBackground(Color.BLACK);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				connect.removeScrollBar(window);
			}
		});
		setBounds(100, 100, 785, 525);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		setTitle("Chat: " + connect.getChannelName());
		
		scrollPane = new JScrollPane();
		scrollPane. setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane. setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		txtChat = new JTextPane(connect.getStyledChat());
		txtChat.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				MouseHandler.updateMouse(window, txtChat, e);
			}
		});
		txtChat.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				MouseHandler.executeClick(txtChat, e);
			}
		});
		scrollPane.setViewportView(txtChat);
		txtChat.setEditable(false);
		txtChat.setBackground(Color.BLACK);
		
		txtSend = new JTextField();
		txtSend.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					connect.sendMessage(txtSend.getText());
					txtSend.setText("");
				}
				else if(e.getKeyCode()==KeyEvent.VK_UP){
					txtSend.setText(connect.getMessageHistory().getReverse());
				}
				else if(e.getKeyCode()==KeyEvent.VK_DOWN){
					txtSend.setText(connect.getMessageHistory().get());
				}
			}
		});
		txtSend.setForeground(Color.WHITE);
		txtSend.setBackground(Color.BLACK);
		contentPane.add(txtSend, BorderLayout.SOUTH);
		txtSend.setColumns(10);
		
		connect.addScrollBar(this);
	}

	@Override
	public void updateScrollBar(ChatConnection connect) {
		ScrollManager.scrollDown(scrollPane, txtChat);
	}

}
