package twitch.gui.splash;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * Splash screen on program launch
 */
public class StartupScreen {
	
	public static final Object SPLASHSCREEN_LOCK = new Object();

	public static final double MAX_PROGRESS = 100;
	public static final double MIN_PROGRESS = 0;
	
	private static final int PROGRESS_START_X = 100;
	private static int PROGRESS_END_X = 500;
	private static final int PROGRESS_Y_LENGTH = 30;
	private static final int PROGRESS_Y_POS = 300;
	
	private static JFrame frame;
	private static JGraphics graphics;
	private static BufferedImage image;
	
	private static final Color BAR = new Color(50, 50, 50);
	private static double value = 0;
	private static String printText = "";
	
	//Initiate startup screen
	public StartupScreen(){
		try {
			image = ImageIO.read(this.getClass().getResourceAsStream("/res/splash.gif"));
		} catch (IOException e) {}
		frame = new JFrame();
		frame.setSize(image.getWidth(), image.getHeight());
		frame.setUndecorated(true);
		graphics = new JGraphics();
		frame.getContentPane().add(graphics);
		Dimension dimemsion = Toolkit.getDefaultToolkit().getScreenSize();
	    frame.setLocation(dimemsion.width/2-frame.getSize().width/2, dimemsion.height/2-frame.getSize().height/2);
		frame.setBackground(new Color(0, 0, 0, 0)); 
		frame.setVisible(true); 
	}
	
	//Splash frame rendering
	public class JGraphics extends JPanel{

		private static final long serialVersionUID = 1L;

		JGraphics() {
			setDoubleBuffered(true);
			setBackground(Color.WHITE);
			setOpaque(false);
            setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        }
		
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			renderSplashFrame((Graphics2D) g);
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
	    }
	}
	
	//Renders the splash image and process bar
	private void renderSplashFrame(Graphics2D splashG) {
		splashG.drawImage(image, 0, 0, null);
		splashG.setColor(Color.BLACK);
		splashG.drawString(printText + "...", PROGRESS_START_X, PROGRESS_Y_POS + PROGRESS_Y_LENGTH + 20);
		drawProcessBar(splashG);
	}
	
	//Draws the process bar based on initialized components and connections 
	private void drawProcessBar(Graphics2D splashG){
		splashG.setColor(Color.BLACK);
		int xLength = (int) ((int) (PROGRESS_END_X - PROGRESS_START_X) * (value / (MAX_PROGRESS - MIN_PROGRESS)));
		splashG.drawRect(PROGRESS_START_X, PROGRESS_Y_POS, PROGRESS_END_X - PROGRESS_START_X, PROGRESS_Y_LENGTH);
		splashG.setColor(BAR);
		splashG.fillRect(PROGRESS_START_X, PROGRESS_Y_POS, xLength, PROGRESS_Y_LENGTH);
	}
	
	//Closes the frame
	public static void stop(){
		frame.dispose();
	}
	
	//Forces the splash screen to update
	private static void forceUpdate(){
		graphics.repaint();
		//Thread sleep used for slowing graphic updates for reading on-screen text
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {}
	}
	
	//Updates the progress bar amount
	public static void updateProgressValue(StartupScreenValues val){
		updateProgressValue(val.getValue());
	}
	
	public static void updateProgressValue(double val){
		value = val;
		forceUpdate();
	}
	
	//Sets the on-screen text on the splash window
	public static void newProgressText(String text){
		synchronized(SPLASHSCREEN_LOCK){
			printText = text;
			forceUpdate();
		}
	}
}
