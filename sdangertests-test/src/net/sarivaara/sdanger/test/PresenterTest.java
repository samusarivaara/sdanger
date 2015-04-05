package net.sarivaara.sdanger.test;

import java.util.List;

import net.sarivaara.sdanger.IMainView;
import net.sarivaara.sdanger.MainPresenter;
import net.sarivaara.sdanger.location.LocationManagerAPI;
import net.sarivaara.sdanger.location.LocationObserver;
import net.sarivaara.sdanger.model.Venue;
import android.test.AndroidTestCase;

public class PresenterTest extends AndroidTestCase {

	private class MockView implements IMainView {

		boolean mShowingSearchProgress = false;
		int mVenueCount = 0;
		Status mCurrentStatus;
		boolean mQueryReturned; // after orientation change
		
		@Override
		public void showSearchProgress(boolean show) {
			
			mShowingSearchProgress = show;
		}

		@Override
		public void setVenues(List<Venue> items) {
			mVenueCount = items.size();			
		}

		@Override
		public void setStatusText(Status statusText, float accuracyInMeters) {
			mCurrentStatus = statusText;			
		}

		@Override
		public void setQueryString(String query) {			
			mQueryReturned = true;
		}

		@Override
		public void showErrorMessage(ErrorMessage error) {
			// TODO: Later
			
		}		
	}
	
	private class MockLocator implements LocationManagerAPI {

		LocationObserver mLocationObserver;
		String mLocationString;
		float mAccuracy;
		boolean mLocationServicesEnabled;
		
		@Override
		public void setLocationObserver(LocationObserver observer) {
			mLocationObserver = observer;
			
		}

		@Override
		public String getLocation() {
			
			return mLocationString;
		}

		@Override
		public float getAccuracy() {
			return mAccuracy;
		}

		@Override
		public boolean locationServicesEnabled() {

			return mLocationServicesEnabled;
		}
		
		void callLocationObserver() {
			mLocationObserver.onLocationUpdated(mLocationString);
		}
		
	}
	
	public void testBasic1() {
		
		MockView view = new MockView();
		MockLocator locator = new MockLocator();
		
		MainPresenter p = new MainPresenter(view, getContext(), locator);
		
		p.activityCreated(null);
		p.activityResumed();
		assertNotNull(locator.mLocationObserver);
		p.activityMenuReady();
		p.activityPaused();
		assertNull(locator.mLocationObserver);
		p.activityResumed();						
	}
	
	// TODO: more tests
}
