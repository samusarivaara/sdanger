package net.sarivaara.sdanger.test;

import android.test.AndroidTestCase;
import net.sarivaara.sdanger.rest.foursquare.CommandGetVenues;


public class CommandCreationTest extends AndroidTestCase {
	
	public void testUrlGeneration() {

		String valid1 = "https://api.foursquare.com/v2/venues/search?" + 
		"&client_secret=GYNP4URASNYRNRGXR5UEN2TGTKJHXY5FGSAXTIHXEUG1GYM2" +
		"&client_id=CYEMKOM4OLTP5PHMOFVUJJAMWT5CH5G1JBCYREATW21XLLSZ" + 
		"&m=foursquare&v=20140806&limit=20&query=abba&ll=12.333,11.222";		
		assertEquals(valid1, new CommandGetVenues("12.333,11.222", "abba").getUrlString()); 
		
		String valid2 = "https://api.foursquare.com/v2/venues/search?" + 
		"&client_secret=GYNP4URASNYRNRGXR5UEN2TGTKJHXY5FGSAXTIHXEUG1GYM2" +
		"&client_id=CYEMKOM4OLTP5PHMOFVUJJAMWT5CH5G1JBCYREATW21XLLSZ" + 
		"&m=foursquare&v=20140806&limit=20&query=abba&ll=-12.333,-11.222";		
		
		assertEquals(valid2, new CommandGetVenues("-12.333,-11.222", "abba").getUrlString());
		
		String valid3 = "https://api.foursquare.com/v2/venues/search?" + 
		"&client_secret=GYNP4URASNYRNRGXR5UEN2TGTKJHXY5FGSAXTIHXEUG1GYM2" +
		"&client_id=CYEMKOM4OLTP5PHMOFVUJJAMWT5CH5G1JBCYREATW21XLLSZ" + 
		"&m=foursquare&v=20140806&limit=20&query=abba2&ll=12.1,11.1";		
		assertEquals(valid3, new CommandGetVenues("12.1,11.1", "abba2").getUrlString());
		
		String valid4 = "https://api.foursquare.com/v2/venues/search?" + 
		"&client_secret=GYNP4URASNYRNRGXR5UEN2TGTKJHXY5FGSAXTIHXEUG1GYM2" +
		"&client_id=CYEMKOM4OLTP5PHMOFVUJJAMWT5CH5G1JBCYREATW21XLLSZ" + 
		"&m=foursquare&v=20140806&limit=20&query=abba&ll=12.33333,11.22222";		
		assertEquals(valid4, new CommandGetVenues("12.33333,11.22222", "abba").getUrlString());
		
	}
}
