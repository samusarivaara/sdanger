/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger;

import net.sarivaara.sdanger.location.MyLocationManager;

public class App extends android.app.Application {

	public static final String LOG_TAG = "sdanger";
	
	// Keep location manager alive during app process' life time.
	// to handle Configuration changes (activity/fragment recreates)
	MyLocationManager mLocationManager;
	
	@Override
	public void onCreate() {	
		super.onCreate();
		mLocationManager = new MyLocationManager(this);
	}
	
	public MyLocationManager getLocationManager() {
		return mLocationManager;
	}
	
}
