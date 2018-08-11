package twitch.utils;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import twitch.gui.windows.Window;

/*
 * Dialog box manager
 */
public class Dialogs {

	private static final JFileChooser fileChooser = new JFileChooser();
	
	public static synchronized boolean confirmation(String title, String question){
		return (JOptionPane.showConfirmDialog(null, question, title, JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION);
	}
	
	public static synchronized String input(String title, String question){
		return (JOptionPane.showInputDialog(null, question, title, JOptionPane.DEFAULT_OPTION));
	}
	
	public static synchronized void error(String title, String message){
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
	public static synchronized void information(String title, String message){
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static synchronized void iconMessage(String title, String message, Icon icon){
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE, icon);
	}

	public static synchronized String loadFile(String title) {
		int retrivedValue = fileChooser.showOpenDialog(Window.window);
		if (retrivedValue == JFileChooser.APPROVE_OPTION)
			return fileChooser.getCurrentDirectory().toString() + "\\" + fileChooser.getSelectedFile().getName();
		
		return null;
	}
	
	public static synchronized String saveFile(String title, String defaultFileName) {
		fileChooser.setSelectedFile(new File(defaultFileName));
		int retrivedValue = fileChooser.showSaveDialog(Window.window);
		if (retrivedValue == JFileChooser.APPROVE_OPTION)
			return fileChooser.getCurrentDirectory().toString() + "\\" + fileChooser.getSelectedFile().getName();
		
		return null;
	}
	
	public static synchronized boolean checkConnected(){
		if(Window.getCurrentConnection() == null || !Window.getCurrentConnection().isConnected()){
			Dialogs.error("Error", "You are currently not connected to a channel.");
			return false;
		}
		return true;
	}
}
