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

public class MyLocationManager implements LocationManagerAPI {
	
	private LocationObserver mObserver; 
	
	// Last known location
	// LL decimal format for example "44.3,37.2","32.8400,-117.2769"
	private String mLocationString;
	private float mLastAccuracyInMeters;
	private Location mBestCurrentLocation;
	
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
	
	private void saveLocationString(Location location) {
		
		// Use google's example location strategy
		if (location != null && LocationStrategy.isBetterLocation(location, mBestCurrentLocation)) {
			mBestCurrentLocation = location;
			mLocationString = Location.convert(location.getLatitude(), Location.FORMAT_DEGREES) + "," + Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
			mLastAccuracyInMeters = location.getAccuracy();
		}
	}
	
	LocationListener mLocationListener = new LocationListener() {
	    
		public void onLocationChanged(Location location) {	      
			if (location != null) {
				synchronized(this) {
					saveLocationString(location);
					if (mObserver != null) {
						mObserver.onLocationUpdated(mLocationString);
					}
				}
			}
		}
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
			stopGettingLocationUpdates();
		} else {
			startGettingLocationUpdates();
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
		
	private void stopGettingLocationUpdates() {
		mLocationManager.removeUpdates(mLocationListener);
	}
	
	private void startGettingLocationUpdates() {
		
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MIN_UPDATE_FREQUENCY_MS, MIN_DISTANCE_UPDATE_THRESHOLD_METERS, mLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				MIN_UPDATE_FREQUENCY_MS, MIN_DISTANCE_UPDATE_THRESHOLD_METERS, mLocationListener);
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
