package twitch.gui.panels;

import javax.swing.JPanel;

import java.awt.Image;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import twitch.channels.GameInfo;

public class GameInfoPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private GameInfo info;
	
	/**
	 * Create the panel.
	 */
	public GameInfoPanel(GameInfo info) {
		this.info = info;
		
		JLabel lblGame = new JLabel("New label");
		lblGame.setIcon(new ImageIcon(info.getBox().getImage().getScaledInstance(60, 100, Image.SCALE_DEFAULT)));
		
		JLabel lblGameName = new JLabel("game name");
		lblGameName.setText(info.getName());
		
		JLabel lblViewers = new JLabel("viewers");
		lblViewers.setText("Viewers: " + info.getPopularity());
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblGame, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblGameName, GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
						.addComponent(lblViewers))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
					.addComponent(lblGame, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblGameName)
						.addPreferredGap(ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
						.addComponent(lblViewers)
						.addContainerGap()))
		);
		setLayout(groupLayout);

	}
	
	public GameInfo getGameInfo(){
		return info;
	}
}
