/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger.location;

public interface LocationObserver {
	/*
	 * @param locationString, LL string for example "44.3,37.2"
	 */
	void onLocationUpdated(String locationString);
}
