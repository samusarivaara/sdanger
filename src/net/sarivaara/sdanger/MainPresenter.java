/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import net.sarivaara.sdanger.location.LocationObserver;
import net.sarivaara.sdanger.location.MyLocationManager;
import net.sarivaara.sdanger.model.Venue;
import net.sarivaara.sdanger.rest.Command;
import net.sarivaara.sdanger.rest.CommandObserver;
import net.sarivaara.sdanger.rest.HttpExecutor;
import net.sarivaara.sdanger.rest.CommandExecutor;
import net.sarivaara.sdanger.rest.Result;
import net.sarivaara.sdanger.rest.foursquare.CommandGetVenues;

public class MainPresenter implements IMainPresenter, LocationObserver, CommandObserver {

	private IMainView mMainView;	
	// Current CommandExecutor in run, null if not running.
	private CommandExecutor mCurrentCommandExecutor;
	// Location manager, provides location updates
	MyLocationManager mLocationManager;
	// Store last query string for re-queries if location changes
	String mLastQueryString;
	// and last result to be used in saveInstanceState
	// to handle Configuration change (orientation, locale...)
	ArrayList<Venue> mLastResult;
	// and main view status (state)
	IMainView.Status mStatus;
	
	// Main thread handler
	Handler mHandler;
	
	// Data to be saved in Bundle (Configuration changes)
	private static final String BUNDLE_KEY_VENUES_RESULT = "bundle.venues";
	private static final String BUNDLE_KEY_QUERY_STRING = "bundle.querystring";
	
	MainPresenter(IMainView mainView, Context context) {
		
		mMainView = mainView;
		mHandler = new Handler(Looper.getMainLooper());
		App myApp = (App)context.getApplicationContext();
		mLocationManager = myApp.getLocationManager();		
	}
		
	@Override
	public void queryStringModified(String queryString) {
		
		if (queryString != null) {
			
			mLastQueryString = queryString;
			
			// Check if we are executing previous search query.
			if (mCurrentCommandExecutor != null) {
				mCurrentCommandExecutor.cancel(true);
				mCurrentCommandExecutor.setCommandObserver(null); // release reference
			}
			mCurrentCommandExecutor = new HttpExecutor(); // Creates AsyncTask 
			mCurrentCommandExecutor.setCommandObserver(this);
			CommandGetVenues command = new CommandGetVenues(mLocationManager.getLocation(), queryString);
			mCurrentCommandExecutor.execute(command); // run AsyncTask
			// Now wait for CommandObserver callback			
			mMainView.showSearchProgress(true);
		}
	}
	
	@Override
	public void activityCreated(Bundle data) {
		
		if (data != null) {
		
			mLastQueryString = data.getString(BUNDLE_KEY_QUERY_STRING, null);
			mLastResult = data.getParcelableArrayList(BUNDLE_KEY_VENUES_RESULT);
			
			if (mLastResult != null) {
				mMainView.setVenues(mLastResult);
			}
		}
	}

	@Override
	public void activityResumed() {
		
		if (!mLocationManager.isNetworkOk()) {
			mMainView.setStatusText(IMainView.Status.EStatusNoNetwork, 0);
		} else if (!mLocationManager.locationServicesEnabled()) {			
			mMainView.setStatusText(IMainView.Status.EStatusNoLocation, 0);			
		} else if (mLocationManager.getLocation() == null) {
			mMainView.setStatusText(IMainView.Status.EStatusFirstLocationQueryOnGoing, 0);
		} else {
			mMainView.setStatusText(IMainView.Status.EStatusNoSearchMatches, mLocationManager.getAccuracy());
		}
		
		mLocationManager.setLocationObserver(this);
		mLocationManager.startGettingLocationUpdates();		
	}
	
	@Override
	public void activityMenuReady() {
				
		// return saved bundle value here because
		// menu (ActionBar) is not ready in activityOnCreate
		if (mLastQueryString != null) {
			mMainView.setQueryString(mLastQueryString);
		}		
	}
	
	@Override
	public void activityPaused() {
		
		// Don't consume device's resources (battery) while Activity is paused.
		mLocationManager.setLocationObserver(null);
		mLocationManager.stopGettingLocationUpdates();
	}

	@Override
	public void activityOnSaveInstanceState(Bundle data) {
		
		if (mLastQueryString != null) {
			data.putString(BUNDLE_KEY_QUERY_STRING, mLastQueryString);
		}
		if (mLastResult != null) {
			data.putParcelableArrayList(BUNDLE_KEY_VENUES_RESULT, mLastResult);			
		}		
	}

	@Override
	public void onLocationUpdated(String locationString) {

		// Run safely on UI thread
		mHandler.post(new Runnable() {

			@Override
			public void run() {				
				queryStringModified(mLastQueryString);
			}			
		});
	}

	@Override
	public void onCommandResultReady(Command command, Result result) {
		
		mMainView.showSearchProgress(false);
		
		if (command instanceof CommandGetVenues) {
			if (result.getErrorCode() == Result.RESULT_CODE_OK) {
				ArrayList<Venue> venues = ((CommandGetVenues)command).getVenues();   
				mMainView.setVenues(venues);
				mLastResult = venues;
			} else {
				// TODO: this could be improved for more user friendly errors strings.
				String error = result.getResultString();
				mMainView.showErrorMessage(error != null ?
						IMainView.ErrorMessage.EHttp : IMainView.ErrorMessage.EGeneric);
			}
				
		}				
	}
}
