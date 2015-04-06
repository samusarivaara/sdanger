/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger.test;

import java.util.concurrent.CountDownLatch;

import net.sarivaara.sdanger.rest.Command;
import net.sarivaara.sdanger.rest.CommandExecutor;
import net.sarivaara.sdanger.rest.CommandObserver;
import net.sarivaara.sdanger.rest.Result;
import net.sarivaara.sdanger.rest.foursquare.CommandGetVenues;
import android.test.AndroidTestCase;

public class CommandExecutionTest extends AndroidTestCase implements CommandObserver {
	
	volatile Result mResult;
	
	// HTTP layer mock
	private class MockExecutor extends CommandExecutor {

		@Override
		public void executeNetworkCommunication(Command command) {			
		
			assertEquals(Result.RESULT_CODE_OK, command.getResult().getErrorCode());			
		}				
	}
	
	// For waiting asynchronous call.
	CountDownLatch mCountDownLatch;
	
	/*
	 * Basic test to check Command, Executor, Result cooperation.
	 */
	public void testCommandExecute1() {
		commandExecutionResCheck();
	}
		
	private void commandExecutionResCheck() {		
		
		MockExecutor mockExecutor = new MockExecutor();		
		CommandGetVenues cmd = new CommandGetVenues("123.1,-123,1", "sahan");
		mCountDownLatch = new CountDownLatch(1);
		mockExecutor.setCommandObserver(this);
		mockExecutor.execute(cmd);
		waitExecution();
		 
		assertEquals(Result.RESULT_CODE_OK, mResult.getErrorCode());							
	}		

	private void waitExecution() {
			
		try {
			mCountDownLatch.await();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}		
	}	

	@Override
	public void onCommandResultReady(Command command, Result result) {
				
		mResult = result;		
		mCountDownLatch.countDown();		
	}
}
