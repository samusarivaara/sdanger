/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger.rest;

/*
 * Command's life cycle:
 * 1) Command is created and url query string is generated.
 * 2) CommandExecutor calls getUrlString() and executes query (HTTP)
 * 3) CommandExecutor sets Result or/and json result (method setJsonResult())
 * 5) CommandExecutor check cancel.
 * 6) CommandExecutor calls convertFromJsonToJavaObject(), Result is set.
 * 7) CommandExecutor calls CommandObserver
 */
public interface Command {
	
	public String getUrlString();	
	public void setJsonResult(String jsonString);
	public void convertFromJsonToJavaObject();
	public Result getResult();	
}
