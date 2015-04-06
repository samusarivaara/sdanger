/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger;

import net.sarivaara.sdanger.location.LocationManagerAPI;
import net.sarivaara.sdanger.location.MyLocationManager;

/*
 * Application class. References to object(s) that should be available
 * during our process life time.
 */
public class App extends android.app.Application {	
	
	// Keep location manager alive during app process' life time.
	// It keeps last location even if it's not available via LocationManager.
	LocationManagerAPI mLocationManager;
	
	@Override
	public void onCreate() {	
		super.onCreate();
		mLocationManager = new MyLocationManager(this);
	}
	
	public LocationManagerAPI getLocationManager() {
		return mLocationManager;
	}
	
}
