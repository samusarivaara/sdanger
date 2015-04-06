/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
 * Utility class. Only static methods here.
 */
public class Utils {
	
	// Code from http://stackoverflow.com/questions/10009804/check-network-connection-android
	// NOTE: does not check other than WIFI/Mobile, could be improved.
	public static boolean isNetworkOk(Context appContext) {
		
		boolean status=false;
		
		try{
			ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);			
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
