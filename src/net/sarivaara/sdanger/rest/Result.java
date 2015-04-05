/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger.rest;

/*
 * Container for HTTP error code and response string. 
 */
public class Result {
	
	public static final int RESULT_CODE_OK = 200;
	public static final int RESULT_RESPONSE_PARSING_FAILED = -1000; // usually JsonException
		
	// HTTP error code. If other than RESULT_CODE_OK, result string is not valid. 
	private int mErrorCode = RESULT_CODE_OK;
	
	// May contain java exception information if Exception is thrown during
	// HTTP connection initialization or during IO communication.
	
	private String mResultString;

	public int getErrorCode() {
		return mErrorCode;
	}

	public void setErrorCode(int errorCode) {
		mErrorCode = errorCode;
	}

	public String getResultString() {
		return mResultString;
	}

	public void setResultString(String resultString) {
		mResultString = resultString;
	}
}
