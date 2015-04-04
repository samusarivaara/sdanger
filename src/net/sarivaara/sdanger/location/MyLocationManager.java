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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public class MyLocationManager {
	
	private volatile LocationObserver mObserver; 
	
	// Last known location
	// LL format for example "44.3,37.2", "32.8400,-117.2769"
	private volatile String mLocationString = null;
	private volatile float mLastAccuracyInMeters;
	
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
	
	private void saveLocationString(final Location location) {
		// TODO: Here we should evaluate location based on provider, accuracy and time stamp.
		if (location != null) {
			//if (mLastAccuracyInMeters == 0 || mLastAccuracyInMeters > location.getAccuracy()) {
				mLocationString = Location.convert(location.getLatitude(), Location.FORMAT_DEGREES) + "," + Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
				mLastAccuracyInMeters = location.getAccuracy();
			//}
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
	
	/*
	 * @param LocationObserver Observer or null.
	 * NOTE: Only one allowed, will replace previous observer.
	 */
	public synchronized void setLocationObserver(LocationObserver observer) {
		mObserver = observer;
	}
	
	public String getLocation() {
		return mLocationString;
	}
	
	public float getAccuracy() {
		return mLastAccuracyInMeters;
	}	
		
	public void stopGettingLocationUpdates() {
		mLocationManager.removeUpdates(mLocationListener);
	}
	
	public void startGettingLocationUpdates() {
		
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MIN_UPDATE_FREQUENCY_MS, MIN_DISTANCE_UPDATE_THRESHOLD_METERS, mLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				MIN_UPDATE_FREQUENCY_MS, MIN_DISTANCE_UPDATE_THRESHOLD_METERS, mLocationListener);
	}
	
	public boolean locationServicesEnabled() {
		
		if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
		   !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			
			return false;
		}
		return true;
	}
	
	// Code from http://stackoverflow.com/questions/10009804/check-network-connection-android
	// NOTE: does not check other than WIFI/Mobile, could be improved.
	public boolean isNetworkOk() {
		
		boolean status=false;
		
		try{
			ConnectivityManager cm = (ConnectivityManager) mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);			
		    NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		    if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
		    	status= true;
		    } else {
		    	netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		        if (netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
		                status= true;
		        }
		 }catch(Exception e){
			 e.printStackTrace();  
		     return false;
		 }
		 return status;
	}
}
