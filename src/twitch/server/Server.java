package twitch.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Scanner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import twitch.utils.Dialogs;

/*
 * HTTP Server for future overlays
 */
public class Server {

	private HttpServer server;
	private File location;
	private int port;
	private boolean created = false;
	
	public Server(int port, File location){
		try {
			this.port = port;
			this.location = location;
		} catch (Exception e) {
			Dialogs.error("Error", "Failure to start server.");
		}
	}
	
	//Creates the HTTP server
	private void create() throws Exception{
    	server = HttpServer.create(new InetSocketAddress(port), 0);
    	MainHandler handle = new MainHandler(location);
    	if(!handle.failed()){
    		server.createContext("/", handle);
    		server.setExecutor(null);
    		created = true;
    	}
    }
	
	//Starts the server
	public void start(){
		if(!created)
			try {
				create();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		server.start();
	}
	
	//Stops the server
	public void stop(){
		server.stop(0);
	}
	
	//Handles all connections too the server
	private class MainHandler implements HttpHandler {
		
		private File location;
		private StringBuilder index = new StringBuilder();
		private boolean failed = false;
		
		public MainHandler(File location){
			this.location = location;
			loadFile();
		}
		
		//Loads the index file
		private void loadFile(){
			String finalLoc = location.getAbsolutePath() + "\\index.html";
			try {
				Scanner scanner = new Scanner(new File(finalLoc));
				while(scanner.hasNextLine())
					index.append(scanner.nextLine());
				scanner.close();
			} catch (FileNotFoundException e) {
				Dialogs.error("Error", "Missing file:\n" + finalLoc);
				failed = true;
			}
		}
		
		public boolean failed(){
			return failed;
		}
		
	    public void handle(HttpExchange t) throws IOException {
	      t.sendResponseHeaders(200, index.length());
	      OutputStream os = t.getResponseBody();
	      os.write(index.toString().getBytes());
	      os.close();
	    }
	  }
}
