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

public abstract class CommandExecutor extends AsyncTask<Command, Void, Command> {
				
	CommandObserver mObserver;	
	
	/*
	 * @param Observer or null.
	 * NOTE: Overwrites previous observer.
	 */
	public void setCommandObserver(CommandObserver observer) {
		mObserver = observer;		
	}
	
	public abstract void executeNetworkCommunication(Command command);
	
	/*
	 * 
	 * @param 0 Url to execute. Common parameter are added automatically. 
	 * @return Json string specified by command
	 *         or null if http timeouts/fails.
	 */
	@Override	
	protected Command doInBackground(Command... commands) {
				
		Command command = commands[0];
		// Perf measurement
		long time = SystemClock.elapsedRealtime();
		
		executeNetworkCommunication(command);		
		Log.d("sdanger", String.format("Http communication took %d ms", SystemClock.elapsedRealtime() - time));
		
		// Check cancel before transforming result json string into java object
		if (!isCancelled() && command.getResult().getErrorCode() == Result.RESULT_CODE_OK) { 
			command.convertFromJsonToJavaObject();
		}
		Log.d("sdanger", String.format("Command took totally %d ms", SystemClock.elapsedRealtime() - time));
		return command;		
	}
	
	@Override
    protected void onPostExecute(Command command) {
        
		// call listener safely in main (UI) thread
		if (mObserver != null) {
			mObserver.onCommandResultReady(command, command.getResult());
		}
    }
	
}
