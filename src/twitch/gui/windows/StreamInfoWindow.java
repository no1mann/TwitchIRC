package twitch.gui.windows;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

import twitch.channels.StreamInfo;
import twitch.gui.panels.StreamThumbnailPanel;
import twitch.utils.Dialogs;

public class StreamInfoWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void createStreamInfoWindow(final StreamInfo info) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new StreamInfoWindow(info);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public StreamInfoWindow(StreamInfo info) {
		if(info == null){
			Dialogs.error("Unexpected Error", "Unexpected error when opening stream info window.");
			this.dispose();
			return;
		}
		if(!info.isLive()){
			Dialogs.error("Not Live", info.getChannel() + " is currently not live.");
			this.dispose();
			return;
		}
		setResizable(false);
		setTitle("Stream Info - " + info.getChannel());
		getContentPane().setLayout(new BorderLayout());
		StreamThumbnailPanel contentPane = new StreamThumbnailPanel(info);
		getContentPane().add(contentPane, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

}
