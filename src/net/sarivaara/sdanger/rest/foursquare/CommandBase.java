/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger.rest.foursquare;

import net.sarivaara.sdanger.rest.Result;

/*
 * Class for constructing HTTP get query string and for translating
 * json response string to objects defined by subclasses.
 */
public class CommandBase {

	
	// Our client specific constants.
	
	// FourSquare common params as defined in https://developer.foursquare.com/docs/venues/search
	// API version v2. Date 20140806, foursquare format (m-param)
	
	private static final String BASE_URL      = "https://api.foursquare.com/v2/venues/search?";
	private static final String CLIENT_VERSION_DATE = "20140806";
	private static final String CLIENT_M = "foursquare";
	
	protected String mUrlString;  // HTTP GET command. Parameters added in subclasses.
	protected String mJsonResult; // Command specific json reply string.
	protected final Result mResult;

	protected CommandBase() {
		super();		
		mUrlString = addCommonParams(BASE_URL);	
		mResult = new Result();		
	}
	
	private static String addCommonParams(String urlString) {
		
		String result = urlString;
		result = result + "&client_secret=" + Config.CLIENT_SECRET;
		result = result + "&client_id=" + Config.CLIENT_ID;
		result = result + "&m=" + CLIENT_M;
		result = result + "&v=" + CLIENT_VERSION_DATE;
		
		return result;
	}
	
	public Result getResult() {
		return mResult;
	}
	
	// To ease up unit testing
	public String toString() {
		return mUrlString;
	}
	
}
