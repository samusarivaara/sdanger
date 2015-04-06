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
	
	// Command for testing cancel
	private class MyCommand implements Command {		 
		
		@Override
		public String getUrlString() {			
			return null;
		}

		@Override
		public void setJsonResult(String result) {			
			// just spend time here, so cancel proceed.
			try {
				Thread.currentThread().sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void convertFromJsonToJavaObject() {
			assertTrue("Should not be called, cause cancelled.", false);
		}

		@Override
		public Result getResult() {
			return null;
		}	
	}
	
	/*
	 * Basic test to check Command, Executor, Result cooperation.
	 */
	public void testCommandExecute1() {
		commandExecutionResCheck();
	}

//	public void testCommandCancel() {
//		
//		MockExecutor mockExecutor = new MockExecutor();		
//		
//		MyCommand cmd = new MyCommand();
//		mCountDownLatch = new CountDownLatch(1);		
//		// asserts in Command.convertFromJsonToJavaObject if was not cancelled.
//		mockExecutor.execute(cmd);
//		try {
//			Thread.currentThread().sleep(1000);
//		} catch (InterruptedException e) {			
//			e.printStackTrace();
//		}		
//		mockExecutor.cancel(false);		
//	}

		
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
