package twitch.gui.windows;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Scanner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import twitch.connection.URLLoader;
import twitch.files.Config;
import twitch.files.ConfigName;
import twitch.files.Database;
import twitch.gui.Globals;
import twitch.utils.Dialogs;

/*
 * Token request window for getting the OAuth token
 */
public class TokenRequestWindow {

	private static final String loc = Globals.FILES_LOCATION + "\\http\\token\\";
	
	private static File file = new File(loc + "token_request.html");
	private static File fileFound = new File(loc + "token_found.html");
	private static File fileError = new File(loc + "token_error.html");
	
	private static boolean found;
	private static HttpServer server;
	
	public TokenRequestWindow() {
		try {
			Globals.USERNAME = Dialogs.input("Twitch Username", "Enter Twitch Username:");
			if(Globals.USERNAME == null)
				return;
			createServer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	//Creates the HTTP server and starts it
	private void createServer() throws Exception {
		URLLoader.openUrl(Globals.AUTORIZATION_URL + Globals.AUTORIZATION_PARAM);
		server = HttpServer.create(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), Globals.REDIRECT_URI_PORT), 0);
		server.createContext("/", new ResponseHandler());
		server.setExecutor(null);
        server.start();
        while(!found)
        	Thread.sleep(50);
	}
	
	//Handles HTTP responses from the server
	static class ResponseHandler implements HttpHandler {
		
		//Parses the token request
        @Override
        public void handle(HttpExchange t) throws IOException {
        	String uri = t.getRequestURI().toString();
        	String response = "";
        	//The URI contains the token
        	if(uri.contains("token")){
        		Database login = (Database)Config.getConfig(ConfigName.LOGIN);
        		Globals.TOKEN = uri.substring(uri.indexOf("token=") + "token=".length());
        		login.put("username", Globals.USERNAME);
    			login.put("token", Globals.TOKEN);
    			found = true;
    			login.save();
    			response = loadFile(fileFound);
        	}
        	else if(uri.contains("error")){
        		response = loadFile(fileError);
        		System.exit(0);
        	}
			else 
				response = loadFile(file);
        	
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
        }
        
        @SuppressWarnings("resource")
		private static String loadFile(File file) throws FileNotFoundException{
        	Scanner scanner = new Scanner(file);
        	String str = "";
			while (scanner.hasNextLine()) 
				str += scanner.nextLine() + "\n";
			return str;
        }
    }

}
	