/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger.rest;

/*
 * Wrapper to provider actual executor.
 */
public class MyNetworkAPI implements NetworkAPI {

	@Override
	public CommandExecutor createCommandExecutor() {		
		return new HttpExecutor();
	}
}
