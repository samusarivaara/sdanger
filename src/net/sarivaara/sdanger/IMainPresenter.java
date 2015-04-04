/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger;

import android.os.Bundle;

public interface IMainPresenter {
	
	// Activity life cycle related methods
	public void activityCreated(Bundle data);
	public void activityResumed();
	public void activityPaused();
	public void activityOnSaveInstanceState(Bundle data);
	public void activityMenuReady();
	
	// Our business logic, called every time when user types in search box.
	public void queryStringModified(String queryString);
	
}
