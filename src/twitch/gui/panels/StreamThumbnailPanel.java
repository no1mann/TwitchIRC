package twitch.gui.panels;

import javax.swing.JPanel;
import javax.swing.JLabel;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import javax.swing.LayoutStyle.ComponentPlacement;

import twitch.channels.StreamInfo;
import twitch.connection.URLLoader;
import twitch.gui.Globals;

public class StreamThumbnailPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private StreamInfoPanel panel;
	
	private static final int WIDTH = 430;
	private static final int HEIGHT = 216;
	
	public StreamThumbnailPanel(StreamInfo info) {
		
		if(!info.isLive()){
			return;
		}
		
		JLabel lblThumbnail = new JLabel("");
		lblThumbnail.setIcon(URLLoader.getImageFromURL(Globals.STREAM_THUMBNAIL(info.getChannel(), WIDTH, HEIGHT)));
		
		panel = new StreamInfoPanel(info, WIDTH);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(lblThumbnail, 0, WIDTH, Short.MAX_VALUE)
				.addComponent(panel, 0, WIDTH, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblThumbnail, GroupLayout.PREFERRED_SIZE, HEIGHT, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, 0, StreamInfoPanel.HEIGHT, Short.MAX_VALUE))
		);
		setLayout(groupLayout);

	}
	
	public StreamInfo getStreamInfo(){
		return panel.getStreamInfo();
	}
}
