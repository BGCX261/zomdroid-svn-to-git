package edu.smu.zomdroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
* @author Corey Cothrum
*
* This class is responsible for communication with the server.
* All communications are currently HTTP GET request. 
* The most important method is the sendUpdate method. 
* This method updates your current status, as well as gets the status
* of other others, in XML.
*
* It then converts the XML into a doc, 
* and sends that back to the calling object.
*/
public class HttpLocUtil {
	
    private static final String BASE_SERVICE_URL = 
		"http://lyle.smu.edu/~coyle/php/zomdroid/";
			
	public HttpLocUtil(){
	}

	public final String getGames(){
		StringBuilder uriBuilder = new StringBuilder(BASE_SERVICE_URL);
    	uriBuilder.append("getGames.php");
    	    	
    	//returns a comma delimited string of available games
    	return sendHttp(uriBuilder.toString());    	
	}
	
	public final void createGame(){
		//TODO: not implemented on the server yet!!
	}
	
	/**
	* Updates your status, and gets all player info
	*/
	public final void sendUpdate(Player p){
		Document doc = null;
		DocumentBuilderFactory dBF;
		dBF = DocumentBuilderFactory.newInstance();
		
		StringBuilder uriBuilder = new StringBuilder(BASE_SERVICE_URL);
        	uriBuilder.append("updateLoc.php?user=" + p.phoneNumber);
        	uriBuilder.append("&game=" + p.gameID);
        	uriBuilder.append("&nickname=" + p.id);
        	uriBuilder.append("&gpx=" + Double.toString(p.gpx));
        	uriBuilder.append("&gpy=" + Double.toString(p.gpy));
        	uriBuilder.append("&zhtype=" + p.zhType);
        	uriBuilder.append("&score=" + Integer.toString(p.score));
        	uriBuilder.append("&active=" + p.isActive);
        	
        	try{
        	String reply = sendHttp(uriBuilder.toString());
        	DocumentBuilder builder = dBF.newDocumentBuilder();
        	InputSource inStream = new InputSource();
        	inStream.setCharacterStream(new java.io.StringReader(reply));
        	doc = builder.parse(inStream);
        	
        	}catch(ParserConfigurationException e) {
        		e.printStackTrace();
        		doc = null;
        	}
        	catch(SAXException e) {
            		e.printStackTrace();
            		doc = null;
        	} catch (IOException e) {
        		doc = null;
				e.printStackTrace();
			} 
        	
        	GameEngine.getInstance().update(doc);        
	}
	
	private final String sendHttp(String str){
		
		HttpGet request = new HttpGet(str);
    	DefaultHttpClient httpClient = new DefaultHttpClient();
    
    	System.out.println("Sending: " + str);
    
    	try {
        		HttpResponse response = httpClient.execute(request);
        		String reply = 
			convertStreamToString(response.getEntity().getContent());
        		
        		return reply;
    	}
    	catch(ClientProtocolException e) {
        		e.printStackTrace();
    	}
    	catch(IOException e) {
        		e.printStackTrace();
    	}
 	
    	return null;
	}
	/**
	* Gets Player info, but doesn't update your status on the server
	*/
	public final void getAllPlayers(){
		//TODO: This is not implemented on the server!!
		Document doc;
		DocumentBuilderFactory dBF;
		dBF = DocumentBuilderFactory.newInstance();
		
		StringBuilder uriBuilder = new StringBuilder(BASE_SERVICE_URL);
		uriBuilder.append("getAllUsers.php");
		
		String reply = sendHttp(uriBuilder.toString());

		try{
            		DocumentBuilder builder = dBF.newDocumentBuilder();
            		InputSource inStream = new InputSource();
            		inStream.setCharacterStream(new java.io.StringReader(reply));
            		doc = builder.parse(inStream);
        	}catch(Exception e){
        		doc = null;
        		System.out.println("Exception: " + e);
        	}
        GameEngine.getInstance().update(doc);
	}

	/**
	* Gets your Player info, but doesn't update your status on the server
	*/
	public final void getMyPlayer(Player p){
		//TODO: This is not implemented on the server!! 
		Document doc;
		DocumentBuilderFactory dBF;
		dBF = DocumentBuilderFactory.newInstance();
		
		StringBuilder uriBuilder = new StringBuilder(BASE_SERVICE_URL);
		uriBuilder.append("getMyUser.php?user=" + p.id);
		
		String reply = sendHttp(uriBuilder.toString());

        try {
            	DocumentBuilder builder = dBF.newDocumentBuilder();
            	InputSource inStream = new InputSource();
            	inStream.setCharacterStream(new java.io.StringReader(reply));
            	doc = builder.parse(inStream);
        	}catch(Exception e){
        		doc = null;
        		System.out.println("Exception: " + e);
        	}
        GameEngine.getInstance().update(doc);
	}
	
	/**
	* References from: http://www.kodejava.org/examples/266.html
	*/
	private final String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                is.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
