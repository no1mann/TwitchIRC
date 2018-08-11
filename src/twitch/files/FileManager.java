package twitch.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import com.esotericsoftware.minlog.Log;

/*
 * Default file manager for saving, and loading files
 */
public class FileManager {

	private File file;
	//Stores contents of the file
	private List<String> lines;
	
	public FileManager(String location){
		this(new File(location));
	}
	
	public FileManager(File location){
		Log.info("Opening file: " + location.getName());
		this.file = location;
		lines = new ArrayList<String>();
		load();
	}
	
	//Loads contents of file line by line
	private void load(){
		try {
			Scanner scanner = new Scanner(file);
			while(scanner.hasNextLine())
				lines.add(scanner.nextLine());
			scanner.close();
		} catch (FileNotFoundException e) {
			Log.info("File not found, creating file: " + file.getName());
			createFile();
		}
	}
	
	//Creates file if the file does not exist
	private void createFile(){
		if(!file.exists()){
			file.getParentFile().mkdirs(); 
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			load();
		}
	}
	
	//Save contents of the file
	public void save(){
		try {
			PrintStream output = new PrintStream(file);
			for(String line: lines)
				output.println(line);
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public File getFile(){
		return file;
	}
	
	public String getFileName(){
		return file.getName();
	}
	
	public String getLocation(){
		return file.getAbsolutePath();
	}
	
	public List<String> getLines(){
		return lines;
	}
	
	public void clearLines(){
		lines.clear();
		lines = new ArrayList<String>();
	}
	
	public void setLine(int num, String line){
		if(num >= lines.size())
			addLine(line);
		else
			lines.set(num, line);
	}
	
	public boolean containsLine(int num){
		return lines.get(num)!=null;
	}
	
	public void setLines(List<String> lines){
		this.lines = lines;
	}
	
	public void addLine(String line){
		lines.add(line);
	}
	
	public void addAllLines(Collection<String> lines){
		lines.addAll(lines);
	}
	
	public String getLine(int num){
		if(num >= lines.size())
			return null;
		return lines.get(num);
	}
	
	public int getLineCount(){
		return lines.size();
	}
	
	public void close(){
		lines.clear();
	}
	
}
