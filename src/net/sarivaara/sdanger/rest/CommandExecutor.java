/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger.rest;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

public abstract class CommandExecutor extends AsyncTask<Command, Void, Result> {
				
	CommandObserver mObserver;
	Command mCommand;
	
	/*
	 * @param Observer or null.
	 * NOTE: Overwrites previous observer.
	 */
	public void setCommandObserver(CommandObserver observer) {
		mObserver = observer;
	}
	
	public abstract Result executeNetworkCommunication(Command command);
	
	/*
	 * 
	 * @param 0 Url to execute. Common parameter are added automatically. 
	 * @return Json string specified by command
	 *         or null if http timeouts/fails.
	 */
	@Override	
	protected Result doInBackground(Command... command) {
		
		mCommand = command[0]; // handling only one
		// Perf measurement
		long time = SystemClock.elapsedRealtime();
		
		Result result = executeNetworkCommunication(mCommand);
		Log.d("sdanger", String.format("Http communication took %d ms", SystemClock.elapsedRealtime() - time));
		
		// Check cancel before transforming result json string into java object
		if (!isCancelled() && result.getErrorCode() == Result.RESULT_CODE_OK) { 
			mCommand.convertFromJsonToJavaObject();
		}
		Log.d("sdanger", String.format("Command took totally %d ms", SystemClock.elapsedRealtime() - time));
		return result;		
	}
	
	@Override
    protected void onPostExecute(Result result) {
        
		// call listener safely in main (UI) thread
		if (mObserver != null) {
			mObserver.onCommandResultReady(mCommand, result);
		}
    }
	
}
