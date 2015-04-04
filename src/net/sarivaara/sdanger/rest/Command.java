/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger.rest;

public interface Command {
	
	public String getUrlString();	
	public void setJsonResult(String result);
	public void convertFromJsonToJavaObject();
}
