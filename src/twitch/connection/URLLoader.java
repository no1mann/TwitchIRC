package twitch.connection;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

import javax.swing.ImageIcon;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.esotericsoftware.minlog.Log;

import twitch.gui.Globals;

public class URLLoader {

	//NUMBER OF ATTEMPTS TILL DENYING REQUEST
	private static final int ATTEMPTS = 3;
	private static final Charset CHARSET = Charset.forName("UTF-8");

	//LOADS IMAGE FROM URL
	public static ImageIcon getImageFromURL(String url) {
		Log.debug("Loaded image from URL: " + url);
		return getImageFromURL(url, 1);
	}
	
	//LOADS IMAGES FROM URL RECURSIVLY - Keeps tract of attempts
	private static ImageIcon getImageFromURL(String url, int amount) {
		try {
			ImageIcon icon = new ImageIcon(new URL(url));
			return icon;
		} catch (MalformedURLException e) {
			if(amount == ATTEMPTS)
				Log.error("Error loading image from URL: " + url, e);
			else{
				return getImageFromURL(url, amount+1);
			}
		} 
		return null;
	}
	
	//LOADS IMAGE FROM FILE
	public static ImageIcon getImageFromFile(String loc) {
		try {
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(loc));
			Log.debug("Loaded image from location: " + loc);
			return icon;
		} catch (Exception e) {
			Log.error("Error loading image from location: " + loc, e);
		} 
		return null;
	}
	
	//LOADS WEBSITE SOURCE CODE
	public static String getSiteSourceCode(String url){
		Log.debug("Loading source code: " + url);
		return getSiteSourceCode(url, 1);
	}
	
	//LOADS WEBSITE SOURCE CODE - Keeps tract of attempts
	private static String getSiteSourceCode(String url, int amount){
		Log.debug("Loading source code: " + url);
		try {
			URLConnection connection = new URL(url).openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), CHARSET));
			String inputLine;
			StringBuilder a = new StringBuilder();
			while ((inputLine = in.readLine()) != null)
				a.append(inputLine);
			in.close();
			Log.debug("Source code loaded: " + url);
			return a.toString();
		} catch (Exception e) {
			if(amount == ATTEMPTS){
				//Ignore if FFZ error
				if(!url.contains(Globals.FFZ_DATA("")))
					Log.error("Error loading source code: " + url);
			}
			else
				return getSiteSourceCode(url, amount+1);
		}
		return null;
	}

	//LOADS JSONObject FROM URL
	public static JSONObject getJSONFromURL(String url) {
		Log.debug("Loading JSON Object from: " + url);
		return getJSONFromURL(url, 1);
	}
	
	//LOADS JSONObject FROM URL - Keeps tract of attempts
	private static JSONObject getJSONFromURL(String url, int amount) {
		Log.debug("Loading JSON Object from: " + url);
		try {
			String jsonText = getSiteSourceCode(url);
			Object obj = new JSONParser().parse(jsonText);
			JSONObject json = (JSONObject) obj;
			Log.debug("JSON Object loaded: " + url);
			return json;
		} catch(Exception e){
			if(amount == ATTEMPTS)
				Log.error("Error loading JSON object from: " + url);
			else
				return getJSONFromURL(url, amount+1);
		}
		return null;
	}
	
	//LOADS JSONObject FROM TWITCH API
	public static JSONObject getKrakenAPI(String targetUrl, boolean requiresToken, boolean requiresClientID){
		Log.debug("Loading Twitch Kraken API from: " + targetUrl);
		return getKrakenAPI(targetUrl, requiresToken, requiresClientID, 1);
	}
	
	//LOADS JSONObject FROM TWITCH API - Keeps tract of attempts
	private static JSONObject getKrakenAPI(String targetUrl, boolean requiresToken, boolean requiresClientID, int amount){
		URL url;
		HttpURLConnection connection = null;
		
		try {
			url = new URL(targetUrl);
			connection = (HttpURLConnection) url.openConnection();
			// Request properties
			connection.setRequestProperty("Accept", "application/gzip");
			connection.setRequestProperty("Accept-Encoding", "gzip");
			if(requiresClientID)
				connection.setRequestProperty("Client-ID", Globals.CLIENT_ID);
			if(requiresToken)
				connection.setRequestProperty("Authorization", "OAuth " + Globals.TOKEN);

			connection.setRequestMethod("GET");
			// Read response
			InputStream input = connection.getInputStream();
			if ("gzip".equals(connection.getContentEncoding()))
				input = new GZIPInputStream(input);
			
			StringBuilder response;
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, CHARSET))) {
				String line;
				response = new StringBuilder();
				while ((line = reader.readLine()) != null)
					response.append(line);
			}
			JSONParser parser = new JSONParser();
			Object obj;
			obj = parser.parse(response.toString());
			JSONObject json = (JSONObject) obj;
			Log.debug("Twitch Kraken API loaded: " + targetUrl);
			return json;
		} catch(Exception e){
			System.out.println(e);
			if(amount == ATTEMPTS)
				Log.error("Error loading Twitch Kraken API: " + targetUrl, e);
			else
				return getKrakenAPI(targetUrl, requiresToken, requiresClientID, amount+1);
		} finally {
			if (connection != null)
				connection.disconnect();
		}
		return null;
	}
	
	//OPENS DEFAULT WEB BROWSER AND GOES TO URL
	public static boolean openUrl(String url) {
		Log.debug("Opening browser to " + url);
		if (url == null)
			return false;
		URI parsed = null;
		try {
			parsed = new URI(url);
		} catch (URISyntaxException ex) {
			Log.error("Invalid URL " + url, ex);
		}
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			try {
				Desktop.getDesktop().browse(parsed);
				Log.debug("Browser opened :" + url);
				return true;
			} catch (IOException ex) {
			}
		} else {
			Log.debug("Error finding browser");
			return false;
		}
		return false;
	}
	
	//Formats URL escape characters
	public static String formatString(String url){
		url = url.replace(" ", "%20");
		return url;
	}
}
