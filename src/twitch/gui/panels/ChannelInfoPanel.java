package twitch.gui.panels;

import javax.swing.JPanel;

import java.awt.Image;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import twitch.channels.ChannelInfo;
import twitch.utils.AdvancedString;
import twitch.utils.Formats;

public class ChannelInfoPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public int WIDTH = 370;
	public static final int HEIGHT = 58;
	private ChannelInfo info;
	
	public ChannelInfoPanel(ChannelInfo info, int width) {
		this.WIDTH = width;
		this.info = info;
		
		JLabel lblLogo = new JLabel("");
		if(info.getLogo()!=null){
			Image logo = info.getLogo().getScaledInstance(HEIGHT, HEIGHT, Image.SCALE_DEFAULT);
			lblLogo.setIcon(new ImageIcon(logo));	
		}
		
		JLabel lblChannelName = new JLabel();
		lblChannelName.setText(info.getDisplayName());
		lblChannelName.setAlignmentY(1.0f);

		
		JLabel lblGame = new JLabel(AdvancedString.ellipsize(lblChannelName.getFont(), "Playing: " + info.getGame(), WIDTH - HEIGHT - 10));
		lblGame.setAlignmentY(1.0f);
		
		JLabel lblData = new JLabel("data");
		String print = "Followers: " + Formats.numberCommaFormatter.format(info.getFollowers()) + " - Views: " + Formats.numberCommaFormatter.format(info.getViews());
		if(info.isPartnered()){
			print += " (Partnered)";
		}
		lblData.setText(print);
		lblData.setAlignmentY(1.0f);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblLogo, GroupLayout.PREFERRED_SIZE, HEIGHT, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblChannelName, GroupLayout.PREFERRED_SIZE, WIDTH, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblGame, GroupLayout.PREFERRED_SIZE, WIDTH, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblData, GroupLayout.PREFERRED_SIZE, WIDTH, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(85, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblChannelName)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblGame)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblData))
						.addComponent(lblLogo, GroupLayout.PREFERRED_SIZE, HEIGHT, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}

	public ChannelInfo getChannelInfo(){
		return info;
	}
	
}
