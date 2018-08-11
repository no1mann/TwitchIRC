package twitch.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;

public class JPanelList extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private JPanel mainList;
	private HashSet<JPanel> panels;

    public JPanelList() {
    	panels = new HashSet<JPanel>();
        setLayout(new BorderLayout());

        mainList = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        mainList.add(new JPanel(), gbc);

        add(new JScrollPane(mainList));

    }
    
    public void addPanel(JPanel panel, boolean redraw){
    	addPanel(panel, redraw, 0);
    }
    
    public void addPanel(JPanel panel, boolean redraw, int spaceBelow){
    	panels.add(panel);
    	panel.setBorder(new CompoundBorder( 
    		    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
    		    BorderFactory.createMatteBorder(0, 0, spaceBelow, 0, panel.getBackground())));
    	GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainList.add(panel, gbc, 0);
		if(redraw){
			redraw();
		}
    }
    
    public void addPanels(Collection<JPanel> panels){
    	for(JPanel panel : panels){
    		addPanel(panel, false);
    	}
    	redraw();
    }
    
    public void removePanel(JPanel panel){
    	if(panels.contains(panel)){
    		panels.remove(panel);
    		mainList.remove(panel);
    		redraw();
    	}
    }
    
    public void clearPanels(){
    	for(JPanel panel : panels){
    		mainList.remove(panel);
    	}
    	redraw();
    }
    
    public void redraw(){
    	validate();
		repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 500);
    }
}
