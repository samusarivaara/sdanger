package net.sarivaara.sdanger.test;

import java.util.List;

import net.sarivaara.sdanger.IMainView;
import net.sarivaara.sdanger.MainPresenter;
import net.sarivaara.sdanger.location.LocationManagerAPI;
import net.sarivaara.sdanger.location.LocationObserver;
import net.sarivaara.sdanger.model.Venue;
import net.sarivaara.sdanger.rest.Command;
import net.sarivaara.sdanger.rest.CommandExecutor;
import net.sarivaara.sdanger.rest.NetworkAPI;
import net.sarivaara.sdanger.rest.Result;
import net.sarivaara.sdanger.rest.foursquare.CommandGetVenues;
import android.os.Bundle;
import android.test.AndroidTestCase;

public class PresenterTest extends AndroidTestCase {

	private class MockView implements IMainView {

		boolean mShowingSearchProgress = false;		
		Status mCurrentStatus;
		String mQueryReturned; // after orientation change
		
		@Override
		public void showSearchProgress(boolean show) {
			
			mShowingSearchProgress = show;
		}

		@Override
		public void setVenues(List<Venue> items) {				
		}

		@Override
		public void setStatusText(Status statusText, float accuracyInMeters) {
			mCurrentStatus = statusText;			
		}

		@Override
		public void setQueryString(String query) {			
			mQueryReturned = query;
		}

		@Override
		public void showErrorMessage(ErrorMessage error) {
			// not covered by unit tests
			
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
		
		void callLocationObserver(String locationString) {
			mLocationObserver.onLocationUpdated(locationString);
		}		
	}
	
	// HTTP layer mock
	public class MockExecutor extends CommandExecutor {
		
		boolean mWasCalled = false;
		
		public MockExecutor() {			
		}
		
		@Override
		public void executeNetworkCommunication(Command command) {		
			mWasCalled = true;
		}
	}
	
	private class MockNetworkAPI implements NetworkAPI {
		
		MockExecutor mMockExecutor;
		
		@Override
		public CommandExecutor createCommandExecutor() {
			mMockExecutor = new PresenterTest.MockExecutor();
			return mMockExecutor;
		}		
	}
	
	MockView mView;
	MockLocator mLocator;
	MockNetworkAPI mNetwork;	
	MainPresenter mPresenter;
	
		
	protected void setUp() throws java.lang.Exception  {
		
		mView = new MockView();
		mLocator = new MockLocator();
		mNetwork = new MockNetworkAPI();	
		mPresenter = new MainPresenter(mView, getContext(), mLocator, mNetwork);
	}
	
	protected void tearDown() throws java.lang.Exception {
		
		mView = null;
		mLocator = null;
		mNetwork = null;	
		mPresenter = null;
	}
	
	
	// Typical user flow.
	public void testBasicFlow() {
				
		mPresenter.activityCreated(null);
		mPresenter.activityResumed();
		mPresenter.activityMenuReady();
		mPresenter.queryStringModified("a", true); // user types 'a'
		assertNotNull(mNetwork.mMockExecutor); // executor created = query was at least tried to execute
		mNetwork.mMockExecutor = null;
		mPresenter.queryStringModified("ab", true); // user types 'ab'
		assertNotNull(mNetwork.mMockExecutor);
		mNetwork.mMockExecutor = null;
		mLocator.callLocationObserver("123.22001,123.22001"); // Location updated by LocationManager
		assertNotNull(mNetwork.mMockExecutor); // executor created = query was executed		
	}
	
	// Test that query string is returned to search view after
	// orientation change.
	public void testOrientationChange() {
		
		Bundle b = new Bundle();
		
		mPresenter.activityCreated(null);
		mPresenter.activityResumed();		
		mPresenter.activityMenuReady();
		mPresenter.queryStringModified("queryString", true);
		mPresenter.activityOnSaveInstanceState(b);
		mPresenter.activityDestroy();

		// Second activity after Configuration change
		MainPresenter p2 = new MainPresenter(mView, getContext(), mLocator, mNetwork);		
		
		p2.activityCreated(b);
		p2.activityResumed();		
		p2.activityMenuReady();
		// Now we should have query prefilled in search view
		assertEquals("queryString", mView.mQueryReturned);		
	}
	
	// Test error state keeping
	public void testStatusState() {
		
		mLocator.mLocationServicesEnabled = false;
		
		mPresenter.activityCreated(null);
		mPresenter.activityResumed();
		mPresenter.activityMenuReady();
		assertEquals(mView.mCurrentStatus, IMainView.Status.EStatusNoLocation);		
	}
	
	// Test that location observing is done in onResume/onPause pairs.
	public void testLocationObserverHooks() {
		
		mPresenter.activityCreated(null);
		mPresenter.activityResumed();
		assertNotNull(mLocator.mLocationObserver);
		mPresenter.activityMenuReady();
		mPresenter.activityPaused();
		assertNull(mLocator.mLocationObserver);
		mPresenter.activityResumed();						
	}
	
	// Test that process indicator is command when command is under execution.
	public void testProgressCommanding() {
		
		mPresenter.activityCreated(null);
		mPresenter.activityResumed();
		mPresenter.activityMenuReady();
		mPresenter.queryStringModified("abba", true);
		assertTrue(mView.mShowingSearchProgress);
		mPresenter.onCommandResultReady(new CommandGetVenues("xxx", "abba"), new Result());
		assertFalse(mView.mShowingSearchProgress);								
	}
}
