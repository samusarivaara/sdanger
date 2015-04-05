package net.sarivaara.sdanger.test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import net.sarivaara.sdanger.model.Venue;
import net.sarivaara.sdanger.rest.Command;
import net.sarivaara.sdanger.rest.CommandExecutor;
import net.sarivaara.sdanger.rest.CommandObserver;
import net.sarivaara.sdanger.rest.Result;
import net.sarivaara.sdanger.rest.foursquare.CommandGetVenues;
import android.test.AndroidTestCase;
 
public class JsonParsingTest extends AndroidTestCase implements CommandObserver {
	
	volatile Result mResult;
	
	// HTTP layer mock
	private class MockExecutor extends CommandExecutor {
	
		String jsonResult;

		@Override
		public void executeNetworkCommunication(Command command) {
			
			command.getResult().setErrorCode(Result.RESULT_CODE_OK);			
			command.setJsonResult(jsonResult);
						
		}				
	}
	
	CountDownLatch mCountDownLatch;
	
	public void testInvalidJson1() {
		// crashes test if parsing fails.
		ArrayList<Venue> venues = executeCommand("response xxxx");				
		assertEquals(0, venues.size());
		assertEquals(Result.RESULT_RESPONSE_PARSING_FAILED, mResult.getErrorCode());
	}
	
	public void testInvalidJson2() {
		ArrayList<Venue> venues = executeCommand("{\"meta\":{\"code\":200},\"response\":{\"venues\"");				
		assertEquals(0, venues.size());
		assertEquals(Result.RESULT_RESPONSE_PARSING_FAILED, mResult.getErrorCode());
	}

	public void testInvalidJson3() {
		String invalid3 = "{\"meta\":{\"code\":200},\"response\":{\"venues\""+
		":[{\"id\":\"5017d04ae4b07d4792f5ac69\",\"name\":";
		ArrayList<Venue> venues = executeCommand(invalid3);				
		assertEquals(0, venues.size());
		assertEquals(Result.RESULT_RESPONSE_PARSING_FAILED, mResult.getErrorCode());
	}
	
	public void testValidJson1() {
		
		// 3 matches

		String valid1 = "{\"meta\":{\"code\":200},\"response\":{\"venues\""+
		":[{\"id\":\"5017d04ae4b07d4792f5ac69\",\"name\":\"Pateniemen sahan museo\""+
		",\"location\":{\"address\":\"Sahantie 21\",\"lat\":65.08710711674112,\"lng\""+
		":25.391271114349365,\"distance\":923,\"postalCode\":\"90800\",\"cc\":\"FI\","+
		"\"country\":\"Suomi\",\"formattedAddress\":[\"Sahantie 21\",\"90800\",\"Suomi\"]},"+
		"\"categories\":[{\"id\":\"4bf58dd8d48988d190941735\",\"name\":\"History Museum\","+
		"\"pluralName\":\"History Museums\",\"shortName\":\"History Museum\",\"icon\":"+
		"{\"prefix\":\"https://ss3.4sqi.net/img/categories_v2/arts_entertainment/"+
		"museum_history_\",\"suffix\":\".png\"},\"primary\":true}],\"verified\":false,\"stats\""+
		":{\"checkinsCount\":2,\"usersCount\":2,\"tipCount\":0},\"specials\":{\"count\":0,\"items\""+
		":[]},\"hereNow\":{\"count\":0,\"summary\":\"Nobody here\",\"groups\":[]},\"referralId\":\""+
		"v-1428063963\"},{\"id\":\"500fc051d86ca281958129c2\",\"name\":\"Sahankartano\",\"location\""+
		":{\"lat\":65.08712067497946,\"lng\":25.391249656677246,\"distance\":924,\"postalCode\":\"90800\""+
		",\"cc\":\"FI\",\"city\":\"Oulu\",\"state\":\"Oulun Lääni\",\"country\":\"Suomi\",\"formattedAddress\""+
		":[\"90800 Oulu\",\"Suomi\"]},\"categories\":[{\"id\":\"4f2a25ac4b909258e854f55f\",\"name\":"+
		"\"Neighborhood\",\"pluralName\":\"Neighborhoods\",\"shortName\":\"Neighborhood\",\"icon\":{\"prefix\""+
		":\"https://ss3.4sqi.net/img/categories_v2/parks_outdoors/neighborhood_\",\"suffix\":\".png\"},\""+
		"primary\":true}],\"verified\":false,\"stats\":{\"checkinsCount\":67,\"usersCount\":6,\"tipCount\":0},\""+
		"specials\":{\"count\":0,\"items\":[]},\"hereNow\":{\"count\":0,\"summary\":\"Nobody here\",\"groups\":[]},"+
		"\"referralId\":\"v-1428063963\"},{\"id\":\"5389c216498e5e2ed87e362e\",\"name\":\"Korkeasaaren sahan rauniot\","+
		"\"location\":{\"address\":\"Pikisaari\",\"lat\":65.02004204179337,\"lng\":25.452537121169893,\"distance\":"+
		"7193,\"cc\":\"FI\",\"city\":\"Oulu\",\"state\":\"Oulun Lääni\",\"country\":\"Suomi\",\"formattedAddress\":"
		+"[\"Pikisaari\",\"Oulu\",\"Suomi\"]},\"categories\":[{\"id\":\"4bf58dd8d48988d166941735\",\"name\":"+
		"\"Sculpture Garden\",\"pluralName\":\"Sculpture Gardens\",\"shortName\":\"Sculpture\",\"icon\":"+
		"{\"prefix\":\"https://ss3.4sqi.net/img/categories_v2/parks_outdoors/sculpture_\",\"suffix\":\".png\"},"+
		"\"primary\":true}],\"verified\":false,\"stats\":{\"checkinsCount\":4,\"usersCount\":1,\"tipCount\":0},\"specials\""+
		":{\"count\":0,\"items\":[]},\"hereNow\":{\"count\":0,\"summary\":\"Nobody here\",\"groups\":[]},\""+
		"referralId\":\"v-1428063963\"}]}}";
		
		ArrayList<Venue> venues = executeCommand(valid1);
		 
		assertEquals(Result.RESULT_CODE_OK, mResult.getErrorCode());		
		assertEquals(3, venues.size());
		checkVenue(venues.get(0), "Pateniemen sahan museo", "Sahantie 21\n90800\nSuomi\n", 923);
		checkVenue(venues.get(1), "Sahankartano", "90800 Oulu\nSuomi\n", 924);
		checkVenue(venues.get(2), "Korkeasaaren sahan rauniot", "Pikisaari\nOulu\nSuomi\n", 7193);						
	}
	
	public void testValidJson2() {

		// test 1 match
		
		String valid1 = 
			"{\"meta\":{\"code\":200},\"response\":{\"venues\":[" +
			"{\"id\":\"525ce99411d2499c4cd07287\",\"name\":\"joku\"," +
			"\"location\":{\"address\":\"xxx 8\",\"lat\":65.08110809326172," +
			"\"lng\":25.405683517456055,\"distance\":56,\"cc\":\"FI\",\"city\":\"Oulu\"," +
			"\"state\":\"joo\",\"country\":\"Suomi\"," +
			"\"formattedAddress\":[\"abba\",\"Oulu\",\"Suomi\"]},\"categories\"" +
			":[{\"id\":\"5032891291d4c4b30a586d68\",\"name\":\"Assisted Living\",\"plural" +
			"Name\":\"Assisted Living\",\"shortName\":\"Assisted Living\",\"ic" +
			"on\":{\"prefix\":\"https://ss3.4sqi.net/img/categories_v2/building/apart" +
			"ment_\",\"suffix\":\".png\"},\"primary\":true}],\"verified\":false,\"stat" +
			"s\":{\"checkinsCount\":0,\"usersCount\":0,\"tipCount\":0},\"specials\":{\"coun" +
			"t\":0,\"items\":[]},\"hereNow\":{\"count\":0,\"summary\":\"Nobody here\",\"gro" +
			"ups\":[]},\"referralId\":\"v-1428235344\"}]}}";
		
		ArrayList<Venue> venues = executeCommand(valid1);
		 
		assertEquals(Result.RESULT_CODE_OK, mResult.getErrorCode());		
		assertEquals(1, venues.size());
		checkVenue(venues.get(0), "joku", "abba\nOulu\nSuomi\n", 56);							
	}
	
	public void testValidJson3() {

		// test 1 match, no distance
		
		String valid1 = 
			"{\"meta\":{\"code\":200},\"response\":{\"venues\":[" +
			"{\"id\":\"525ce99411d2499c4cd07287\",\"name\":\"joku\"," +
			"\"location\":{\"address\":\"xxx 8\",\"lat\":65.08110809326172," +
			"\"lng\":25.405683517456055,\"cc\":\"FI\",\"city\":\"Oulu\"," +
			"\"state\":\"joo\",\"country\":\"Suomi\"," +
			"\"formattedAddress\":[\"abba\",\"Oulu\",\"Suomi\"]},\"categories\"" +
			":[{\"id\":\"5032891291d4c4b30a586d68\",\"name\":\"Assisted Living\",\"plural" +
			"Name\":\"Assisted Living\",\"shortName\":\"Assisted Living\",\"ic" +
			"on\":{\"prefix\":\"https://ss3.4sqi.net/img/categories_v2/building/apart" +
			"ment_\",\"suffix\":\".png\"},\"primary\":true}],\"verified\":false,\"stat" +
			"s\":{\"checkinsCount\":0,\"usersCount\":0,\"tipCount\":0},\"specials\":{\"coun" +
			"t\":0,\"items\":[]},\"hereNow\":{\"count\":0,\"summary\":\"Nobody here\",\"gro" +
			"ups\":[]},\"referralId\":\"v-1428235344\"}]}}";
		
		ArrayList<Venue> venues = executeCommand(valid1);
		 
		assertEquals(Result.RESULT_CODE_OK, mResult.getErrorCode());		
		assertEquals(1, venues.size());
		checkVenue(venues.get(0), "joku", "abba\nOulu\nSuomi\n", Venue.DISTANCE_UNKNOWN);							
	}
	
	public void testInValidJson4() {

		// test 1 match, no distance, no formattedAddress
		
		String valid1 = 
			"{\"meta\":{\"code\":200},\"response\":{\"venues\":[" +
			"{\"id\":\"525ce99411d2499c4cd07287\",\"name\":\"joku\"," +
			"\"location\":{\"address\":\"xxx 8\",\"lat\":65.08110809326172," +
			"\"lng\":25.405683517456055,\"cc\":\"FI\",\"city\":\"Oulu\"," +
			"\"state\":\"joo\",\"country\":\"Suomi\"," +
			"},\"categories\"" +
			":[{\"id\":\"5032891291d4c4b30a586d68\",\"name\":\"Assisted Living\",\"plural" +
			"Name\":\"Assisted Living\",\"shortName\":\"Assisted Living\",\"ic" +
			"on\":{\"prefix\":\"https://ss3.4sqi.net/img/categories_v2/building/apart" +
			"ment_\",\"suffix\":\".png\"},\"primary\":true}],\"verified\":false,\"stat" +
			"s\":{\"checkinsCount\":0,\"usersCount\":0,\"tipCount\":0},\"specials\":{\"coun" +
			"t\":0,\"items\":[]},\"hereNow\":{\"count\":0,\"summary\":\"Nobody here\",\"gro" +
			"ups\":[]},\"referralId\":\"v-1428235344\"}]}}";
		
		ArrayList<Venue> venues = executeCommand(valid1);
		 
		assertEquals(Result.RESULT_RESPONSE_PARSING_FAILED, mResult.getErrorCode());		
		assertEquals(0, venues.size()); // should not be listed.								
	}
	
	
	private ArrayList<Venue> executeCommand(String json) {
		
		MockExecutor mockExecutor = new MockExecutor();		
		mockExecutor.jsonResult = json;
		CommandGetVenues cmd = new CommandGetVenues("123.1,-123,1", "sahan"); // params not actually used.
		mCountDownLatch = new CountDownLatch(1);
		mockExecutor.setCommandObserver(this);
		mockExecutor.execute(cmd);
		waitExecution();
		return cmd.getVenues();
	}

	private void checkVenue(Venue venue, String expectedName, String expectedAddress, int expectedDistance) {
		
		assertEquals(expectedName, venue.getName());
		assertEquals(expectedAddress, venue.getAddress());
		assertEquals(expectedDistance, venue.getDistanceInMeters());		
	}

	private void waitExecution() {
			
		try {
			mCountDownLatch.await();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}		
	}	

	@Override
	public void onCommandResultReady(Command command, Result result) {
				
		mResult = result;
		mCountDownLatch.countDown();		
	}
}
