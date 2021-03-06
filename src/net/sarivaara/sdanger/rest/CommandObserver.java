/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger.rest;

public interface CommandObserver {
	
	/*
	 * @param command Reference to command object which was executed. Not null.
	 * @param result command's result. Not null.
	 */
	void onCommandResultReady(Command command, Result result);
}
