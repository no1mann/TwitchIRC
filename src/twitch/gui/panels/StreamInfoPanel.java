package twitch.gui.panels;

import javax.swing.JPanel;

import java.awt.Image;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import twitch.channels.StreamInfo;
import twitch.utils.AdvancedString;
import twitch.utils.Formats;

public class StreamInfoPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public int WIDTH = 370;
	public static final int HEIGHT = 58;
	private StreamInfo info;
	
	public StreamInfoPanel(StreamInfo info, int width) {
		this.WIDTH = width;
		this.info = info;
		
		if(!info.isLive()){
			return;
		}
		
		JLabel lblLogo = new JLabel("");
		if(info.getLogo()!=null){
			Image logo = info.getLogo().getScaledInstance(HEIGHT, HEIGHT, Image.SCALE_DEFAULT);
			lblLogo.setIcon(new ImageIcon(logo));	
		}
		
		JLabel lblTitle = new JLabel();
		lblTitle.setText("title");

		lblTitle.setAlignmentY(1.0f);
		if(info.isPlaylist()){
			lblTitle.setText(AdvancedString.ellipsize(lblTitle.getFont(), info.getStatus() + " (Vodcast)", WIDTH - HEIGHT - 10));
		}
		else{
			lblTitle.setText(AdvancedString.ellipsize(lblTitle.getFont(), info.getStatus(), WIDTH - HEIGHT - 10));
		}
		
		JLabel lblGame = new JLabel(AdvancedString.ellipsize(lblTitle.getFont(), "Playing: " + info.getGame(), WIDTH - HEIGHT - 10));
		lblGame.setAlignmentY(1.0f);
		
		int printFPS = (int) (Math.round((double)info.getFps()/10.0)*10);
		JLabel lblData = new JLabel(info.getDisplayName() + " - " + Formats.numberCommaFormatter.format(info.getViewers()) + " viewers, (" + info.getVideoHeight() + "p)");
		lblData.setAlignmentY(1.0f);
		if(printFPS > 0){
			lblData.setText(info.getDisplayName() + " - " + Formats.numberCommaFormatter.format(info.getViewers()) + " viewers, (" + info.getVideoHeight() + "p" + printFPS + ")");
		}
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblLogo, GroupLayout.PREFERRED_SIZE, HEIGHT, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblTitle, GroupLayout.PREFERRED_SIZE, WIDTH, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblGame, GroupLayout.PREFERRED_SIZE, WIDTH, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblData, GroupLayout.PREFERRED_SIZE, WIDTH, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(85, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblTitle)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblGame)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblData))
						.addComponent(lblLogo, GroupLayout.PREFERRED_SIZE, HEIGHT, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		setLayout(groupLayout);

	}

	public StreamInfo getStreamInfo(){
		return info;
	}
	
}
