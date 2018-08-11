package twitch.gui.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JPanel;

import twitch.gui.windows.Window;
import twitch.message.emotes.ChatEmote;
import twitch.message.emotes.Emote;
import twitch.utils.Dialogs;

public class EmoteWindow extends JPanel implements ActionListener{
	
	private static final Object processLock = new Object();
	private static final long serialVersionUID = 4802918169078331154L;
	public static final double FPS = 120;
	
	private Timer timer;
	private HashSet<EmoteGraphic> renderList = new HashSet<EmoteGraphic>();
	
	//Generates a new emote window instance
	public static EmoteWindow newEmoteWindow(final int width, final int height){
		if(!Window.isConnected()){
			Dialogs.error("Error", "You must connect to a channel before\nthis window can be opened.");
			return null;
		}
		try {
			JFrame emoteView = new JFrame();
			emoteView.setTitle("Emote View");
			emoteView.setSize(width, height);
			EmoteWindow window = new EmoteWindow();
			window.setBounds(0 ,0 ,width ,height);
			emoteView.getContentPane().add(window);
			emoteView.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                	ChatEmote.emoteWindow = null;
                }
            });
			emoteView.setVisible(true);
			return window;
		} catch (Exception e) {
			Dialogs.error("Error", "Error creating Emote View");
			return null;
		}
	}
	
	public EmoteWindow(){
		this.setFocusable(true);
		this.setBackground(Color.GREEN);
		this.setDoubleBuffered(true);
		
		timer = new Timer(0, this);
		timer.start();
		
		repaint();
	}
	
	//Adds emote when a user inputs an emote in chat
	public void addEmote(Emote emote){
		synchronized(processLock){
			renderList.add(new EmoteGraphic(emote, this.getWidth(), this.getHeight()));
		}
	}
	
	//Updates the emote overlay
	@Override
	public void paint(Graphics g){
		super.paintComponent(g);
		Graphics2D graphics = (Graphics2D)g;
		
		//Moves all the emotes down the screen
		synchronized (processLock) {
			Iterator<EmoteGraphic> emoteList = renderList.iterator();
			while (emoteList.hasNext()) {
				EmoteGraphic emote = emoteList.next();
				emote.move();
				if (emote.isVisable()) 
					emote.getImage().paintIcon(this, graphics, (int)emote.getX(), (int)emote.getY());
				else 
					emoteList.remove();
			}
		}
		
		//Syncs the overlay for smoothness
		Toolkit.getDefaultToolkit().sync();
        g.dispose();
		
		try {
			Thread.sleep((long) (1000.0 / FPS));
		} catch (InterruptedException e){}
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		repaint();
	}
}
