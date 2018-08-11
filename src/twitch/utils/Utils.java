package twitch.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import twitch.Main;

/*
 * Random helper methods
 */
public class Utils {
	
	//Loads custom jar files
	public static void restart() throws Exception {
		final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		final File currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());

		System.out.println(currentJar);
		
		// is it a jar file?
		if (!currentJar.getName().endsWith(".jar"))
			return;

		// Build command: java -jar application.jar
		final ArrayList<String> command = new ArrayList<String>();
		command.add(javaBin);
		command.add("-jar");
		command.add(currentJar.getPath());

		final ProcessBuilder builder = new ProcessBuilder(command);
		builder.start();
		System.exit(0);
	}
	
	
	//Prints size of object stored in memory - used for debugging memory issues
	public static void printByteSize(Object obj) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.close();
			System.out.println(baos.size() + "");
		} catch (IOException e) {
			System.out.println("Error printing bytes");
		}
	}
}
