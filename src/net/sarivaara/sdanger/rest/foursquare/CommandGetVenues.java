/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger.rest.foursquare;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import net.sarivaara.sdanger.model.Venue;
import net.sarivaara.sdanger.rest.Command;

// Command specified by https://developer.foursquare.com/docs/venues/search
// NOTE: Not using parameter that does not affect to search result.
// NOTE2: Max 20 results are delivered.

public class CommandGetVenues extends CommandBase implements Command {

	// Result array
	private ArrayList<Venue> mVenues = new ArrayList<Venue>();
	
	/*
	 * @param location Location string in format "123.12345,123.12345"
	 * @param query User's search filter, usually text from Search editor.  
	 */
	public CommandGetVenues(String location, String query) {
				
		super();
		mUrlString += "&limit=20";
		try {
			mUrlString += "&query="+URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// never happens, since UTF-8 is always supported!
			e.printStackTrace();
		}
		mUrlString += "&ll="+location;				
	}
	
	@Override
	public void convertFromJsonToJavaObject() {		
		
		if (mJsonResult != null) {
			
			mVenues.clear();
			// Log.d("sdanger", mJsonResult);

			try {
				
				JSONObject object = new JSONObject(mJsonResult);
				JSONObject response = object.getJSONObject("response");
				JSONArray venues = response.getJSONArray("venues");
								
				for (int i=0;i<venues.length();i++) {
				
					Venue venue = new Venue();
					
					JSONObject jsonVenue = venues.getJSONObject(i);
					venue.setName(jsonVenue.getString("name"));

					// Location parsing
					// Defined by spec:
					// location
					// An object containing none, some, or all of address (street address),
					// crossStreet, city, state, postalCode, country, lat, lng, and distance. 
					// All fields are strings, except for lat, lng, and distance.
					// Distance is measured in meters.
					// Some venues have their locations intentionally hidden for privacy 
					// reasons (such as private residences). If this is the case, the parameter
					// isFuzzed will be set to true, and the lat/lng parameters will have reduced precision. 
					 
					JSONObject location = jsonVenue.getJSONObject("location");
					if (location.has("formattedAddress")) {
						
						JSONArray formattedAddressItems =  location.getJSONArray("formattedAddress");
						StringBuffer sb = new StringBuffer();
						for (int j=0;j<formattedAddressItems.length();j++) {
							sb.append(formattedAddressItems.getString(j));
							sb.append("\n");
						}
						venue.setAddress(sb.toString());						
					}
					if (location.has("distance")) {
						venue.setDistanceInMeters(location.getInt("distance"));
					}					
					mVenues.add(venue);
				}
				
			}catch (JSONException e) {
				Log.d("sdanger" , e.toString());
			}						
		}
	}
	
	public ArrayList<Venue> getVenues() {
		return mVenues;
	}

	@Override
	public String getUrlString() {
		return mUrlString;		
	}

	@Override
	public void setJsonResult(String resultString) {
		mJsonResult = resultString;
	}
}
