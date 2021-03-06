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
import net.sarivaara.sdanger.location.LocationManagerAPI;
import net.sarivaara.sdanger.location.LocationObserver;
import net.sarivaara.sdanger.model.Venue;
import net.sarivaara.sdanger.rest.Command;
import net.sarivaara.sdanger.rest.CommandObserver;
import net.sarivaara.sdanger.rest.MyNetworkAPI;
import net.sarivaara.sdanger.rest.NetworkAPI;
import net.sarivaara.sdanger.rest.CommandExecutor;
import net.sarivaara.sdanger.rest.Result;
import net.sarivaara.sdanger.rest.foursquare.CommandGetVenues;

/*
 * Presenter implementation. Business logic/Use cases:
 * 
 * UC1 User types search - foursquare command(s) are executed immediately and previous one is cancelled.
 * UC2 Location Manager updates location - search is automatically re-queried.
 * UC3 User rotates device - last result is stored in Bundle, not re-queried to avoid unnecessary networking.
 * UC4 User has disabled network (airplane, wifi/3G/4G off) - Error msg is shown.
 * UC5 Error during command execution is shown with a toast. 
 *
 */
public class MainPresenter implements IMainPresenter, LocationObserver, CommandObserver {

	private IMainView mMainView;	
	// Current CommandExecutor in run, null if not running.
	private CommandExecutor mCurrentCommandExecutor;
	// Location manager, provides location updates
	private LocationManagerAPI mLocationManager;	
	// CommandExecutors are created via this interface. 
	private NetworkAPI mNetworkAPI;
	// Store last query string for re-queries if location changes
	private String mLastQueryString;
	// Last result to be used in saveInstanceState
	// to handle Configuration change (orientation, locale...)
	// Could make re-query but that causes unnecessary traffic and
	// slowness.
	private ArrayList<Venue> mLastResult = new ArrayList<Venue>();
	// Store mLastQueryString returned from bundle, so we can detect
	// that queryStringModified() call was not called because
	// we called IMainView.setQueryString().
	private String mLastQueryStringFromBundle;
	
	// Data to be saved in Bundle (Configuration changes)
	private static final String BUNDLE_KEY_VENUES_RESULT = "bundle.venues";
	private static final String BUNDLE_KEY_QUERY_STRING = "bundle.querystring";
	
	Context mContext;
	
	/*
	 * @param mainView View
	 * @param context Activity context.
	 * @param locationManagerAPI Use null for default. None null for unit tests.
	 * @param networkAPI Use null for default. None null for unit tests.  
	 */
	public MainPresenter(IMainView mainView, Context context,
	                     LocationManagerAPI locationManagerAPI, NetworkAPI networkAPI) {
		
		mMainView = mainView;
		mContext = context; 
		if (locationManagerAPI == null) {
			App myApp = (App)context.getApplicationContext();
			mLocationManager = myApp.getLocationManager();
		} else {
			mLocationManager = locationManagerAPI;
		}
		if (networkAPI == null) {
			mNetworkAPI = new MyNetworkAPI();
		} else {
			mNetworkAPI = networkAPI;
		}
	}
		
	@Override
	public void queryStringModified(String queryString, boolean callerIsView) {
		
		// TODO: Performance optimization. Wait 500ms before executing query, so quickly
		// typed (full) words don't cause unnecessary network traffic.
		// Could dramatically reduce hosting costs, user data plan costs. Only minor
		// down side is that user need to wait half second more to get actual result. 
		
		if (queryString != null && mMainView != null) {
			
			if (queryString.equals(mLastQueryStringFromBundle) && callerIsView) {
				// Don't query after orientation change, only if location changes.
				mLastQueryStringFromBundle = null;
				return;
			}
			
			mLastQueryString = queryString;
			
			// Check if we are executing previous search query.
			if (mCurrentCommandExecutor != null) {
				mCurrentCommandExecutor.cancel(true);
				mCurrentCommandExecutor.setCommandObserver(null); // release reference
			}
			mCurrentCommandExecutor = mNetworkAPI.createCommandExecutor(); // Creates AsyncTask 
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
			mLastQueryStringFromBundle = mLastQueryString;
			ArrayList<Venue> result = data.getParcelableArrayList(BUNDLE_KEY_VENUES_RESULT);
			
			if (result != null) {
				mLastResult = result;
				mMainView.setVenues(mLastResult);				
			}
		}
	}

	@Override
	public void activityResumed() {
		
		// Check errors every time when activity resumes.
		updateStatus();				
		mLocationManager.setLocationObserver(this);				
	}
	
	@Override
	public void activityMenuReady() {
				
		// return saved bundle value here because
		// menu (ActionBar) is not ready during activityOnCreate()
		if (mLastQueryString != null) {
			mMainView.setQueryString(mLastQueryString);
		}		
	}
	
	@Override
	public void activityPaused() {
		
		// Don't consume device's resources (battery) while activity is paused.
		mLocationManager.setLocationObserver(null);		
		
		if (mCurrentCommandExecutor != null) {
			mCurrentCommandExecutor.setCommandObserver(null);
		}
	}
	
	@Override
	public void activityDestroy() {
		
		// release reference
		mMainView = null;
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

		// Called always from main thread, so it is safe to
		// call this directly
		queryStringModified(mLastQueryString, false);			
	}
	
	/*
	 * Check errors and set view's status.
	 */
	private void updateStatus() {
		
		if (!Utils.isNetworkOk(mContext)) {
			mMainView.setStatusText(IMainView.Status.EStatusNoNetwork, 0);
		} else if (!mLocationManager.locationServicesEnabled()) {			
			mMainView.setStatusText(IMainView.Status.EStatusNoLocation, 0);			
		} else if (mLocationManager.getLocation() == null) {
			mMainView.setStatusText(IMainView.Status.EStatusFirstLocationQueryOnGoing, 0);
		} else if (mLastResult.size() == 0) {
			mMainView.setStatusText(IMainView.Status.EStatusNoSearchMatches, mLocationManager.getAccuracy());
		}
	}

	@Override
	public void onCommandResultReady(Command command, Result result) {
		
		if (mMainView != null) { // check if activity was destroyed.
			mMainView.showSearchProgress(false);
			
			if (command instanceof CommandGetVenues) {
				if (result.getErrorCode() == Result.RESULT_CODE_OK) {
					ArrayList<Venue> venues = ((CommandGetVenues)command).getVenues();   
					mMainView.setVenues(venues);
					mLastResult = venues;
					if (venues.size() == 0) {
						updateStatus();
					}
					
				} else {
					// TODO: this could be improved for more user friendly errors strings.
					String error = result.getResultString();
					mMainView.showErrorMessage(error != null ?
							IMainView.ErrorMessage.EHttp : IMainView.ErrorMessage.EGeneric);
				}					
			}
		}
	}
}
