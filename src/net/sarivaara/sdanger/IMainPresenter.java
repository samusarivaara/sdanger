/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger;

import android.os.Bundle;

/*
 * MVP's presenter interface. Future improvement: Get rid of Bundle import,
 * so Presenters can be tested without android instrumentation (junit). 
 */
public interface IMainPresenter {
	
	// Activity life cycle related methods
	public void activityCreated(Bundle data);
	public void activityResumed();
	public void activityPaused();
	public void activityDestroy();
	public void activityOnSaveInstanceState(Bundle data);
	public void activityMenuReady();
	
	/* Our business logic, should be called every time when user modifies search string.
	 * 
	 * @param queryString String "" is ok and will return near venues without filtering. Null does nothing.
	 * @param callerIsView Should be always true. False means that caller is implementing class.
	 */
	public void queryStringModified(String queryString, boolean callerIsView);
	
}
