/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger.location;

public interface LocationManagerAPI {

	/*
	 * @param LocationObserver Observer or null.
	 * NOTE: Only one allowed, replaces previous observer.
	 */
	public void setLocationObserver(LocationObserver observer);
	
	/*
	 * Return best location available now.
	 * 
	 * @return LL String in desimal format. Example: "12.12255,23.44456"
	 *         May return null if location not available.  
	 */
	public String getLocation();
	
	/*
	 * Accuracy of getLocation() value. 
	 * 
	 * @return Accuracy in meters. 
	 */
	public float getAccuracy();
	
	/*
	 * @return true if GPS or network location is enabled.  
	 */
	public boolean locationServicesEnabled();
}
