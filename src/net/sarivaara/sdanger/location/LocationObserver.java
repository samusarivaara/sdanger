/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger.location;

/*
 * A simple interface for getting location updates.
 */
public interface LocationObserver {
	/*
	 * @param locationString, Latitude/Longitude string for example "44.32234,37.24444". Decimals may vary.
	 */
	void onLocationUpdated(String locationString);
}
