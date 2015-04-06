/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * HTTP GET executor.
 */
public class HttpExecutor extends CommandExecutor {

	private static final int HTTP_OK = 200;
	
	/*
	 */
	@Override
	public void executeNetworkCommunication(Command command) {				
		
		Result result = command.getResult();
		
	    try {
	    	URL url = new URL(command.getUrlString());
	    	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    	
	    	try {  		
		        connection.setDoInput(true);
		        int responseCode = connection.getResponseCode();
		        result.setErrorCode(responseCode);
		        
		        if (responseCode == HTTP_OK) {
		        	String jsonString = inputStreamToString(connection.getInputStream());
		        	command.setJsonResult(jsonString);		        	
		        }
	    	} finally {
	    		connection.disconnect(); // close streams
	    	}
	    } catch (Exception e) {
	    	result.setResultString(e.toString());
	    	e.printStackTrace();
	    }	    
	}
	
	// Note: caller is responsible to close input stream.
	/*
	 * @param in HTTP connection's input stream.
	 */
	private static String inputStreamToString(InputStream in) throws IOException {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuffer result = new StringBuffer();
		String line;
		
		while((line = reader.readLine()) != null) {
		    result.append(line);
		}
		return result.toString();		
	}
	
}
