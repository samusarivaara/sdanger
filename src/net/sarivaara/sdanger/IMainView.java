/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger;

import java.util.List;

import net.sarivaara.sdanger.model.Venue;

/*
 * MVP's View interface.
 */
public interface IMainView {

	// Status texts to be shown instead of empty list view.
	public enum Status {
		
		EStatusNoLocation,                // No last known location or location services disabled
		EStatusFirstLocationQueryOnGoing, // No last location available, getting location first time. 
		EStatusNoNetwork,                 // Airplane mode or wifi/3G/4G... disabled
		EStatusNoSearchMatches,           // Empty result from back end.
	}
	
	public enum ErrorMessage {
		
		EGeneric, // Something happened during network communication
		EHttp, // Query result was other than HTTP OK. 
	}
	
	/* 
	 * Indicate progress while executing network communication.
	 * 
	 *  @param show true if progress bar need to be shown, 
	 *              otherwise(false) will hide.
	 */	
	public void showSearchProgress(boolean show);
	
	/* 
	 * Updates venues to list view.
	 * 
	 * @param items Items to be shown in list view.
	 */
	public void setVenues(List<Venue> items);
	
	/*
	 * Indicate status in UI main area if search result
	 * is not available. 
	 * 
	 * @param statusText See Status for details.
	 * @param accuracyInMeters Location accuracy information. Optional param, zero if not used.
	 *                         Usually comes with STATUS_NO_SEARCH_MATCHES.
	 */
	public void setStatusText(Status statusText, float accuracyInMeters);
	
	/*
	 * Return query String after Configuration change.
	 * @param query Last query string. 
	 */
	public void setQueryString(String query);
	
	/*
	 * Show toast for generic errors.
	 * @param error See ErrorMessage for details.
	 */
	public void showErrorMessage(ErrorMessage error);

}