/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/*
 * LocationManager needs both network and GPS providers to work for accuracy reasons.
 */
public class MyLocationManager implements LocationManagerAPI {
	
	private LocationObserver mObserver; 
	
	// Last known location presented as LL decimal format
	// for example "44.3,37.2","32.8400,-117.2769"
	private String mLocationString;
	private float mLastAccuracyInMeters;
	private Location mCurrentLocation;

	// Just picked some values to these fields. Works good enough for this app!
	private static final int MIN_UPDATE_FREQUENCY_MS = 10*1000; // 10 sec
	private static final int MIN_DISTANCE_UPDATE_THRESHOLD_METERS = 10; // 10 meters
	
	LocationManager mLocationManager;
	Context mAppContext;
	
	/*
	 * @param Context Application context.
	 */
	public MyLocationManager(Context context) {
		
		mAppContext = context;
		// Get system LocationManager
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// Get last known network location for quicker fix
		Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		saveLocationString(location);		
	}
	
	/*
	 * Stores current location if it's better than previous.
	 * 
	 * @param location Latest location update from location manager.
	 * @return true if location was actually stored.
	 */
	private boolean saveLocationString(Location location) {
				
		// Use google's example location strategy
		if (location != null && LocationStrategy.isBetterLocation(location, mCurrentLocation)) {
			mCurrentLocation = location;
			
			mLocationString = locateToUsFormat(mCurrentLocation);
			mLastAccuracyInMeters = location.getAccuracy();
			return true;
		}
		return false;
	}
	
	// Locate is Locale specific, convert ',' -> '.' if found 
	private String locateToUsFormat(Location location) {
		
		String latitudeString = Location.convert(location.getLatitude(), Location.FORMAT_DEGREES);
		String longitudeString = Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
		return latitudeString.replace(',', '.') + "," + longitudeString.replace(',', '.');
	}
	
	LocationListener mLocationListener = new LocationListener() {
	    
		public void onLocationChanged(Location location) {	      
			if (location != null) {				
				// call observer only if we actually get better
				// location than previous was. 
				if (saveLocationString(location)) {
					if (mObserver != null) {
						mObserver.onLocationUpdated(mLocationString);
					}
				}				
			}
		}
		// These are checked after activity is resumed via locationServicesEnabled().
		// Should be enough for all users :)
	    public void onProviderEnabled(String provider) {}
	    public void onProviderDisabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {			
		}
	};	
	
	@Override
	public void setLocationObserver(LocationObserver observer) {
		mObserver = observer;
		if (mObserver == null) {
			mLocationManager.removeUpdates(mLocationListener);
		} else {
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					MIN_UPDATE_FREQUENCY_MS, MIN_DISTANCE_UPDATE_THRESHOLD_METERS, mLocationListener);
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					MIN_UPDATE_FREQUENCY_MS, MIN_DISTANCE_UPDATE_THRESHOLD_METERS, mLocationListener);
		}
	}
	
	@Override
	public String getLocation() {
		return mLocationString;
	}
	
	@Override
	public float getAccuracy() {
		return mLastAccuracyInMeters;
	}
	
	@Override
	public boolean locationServicesEnabled() {
		
		if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
		   !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			
			return false;
		}
		return true;
	}
}
