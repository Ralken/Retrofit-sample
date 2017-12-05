package cn.ralken.android.http.exception;

import java.io.IOException;

public class InvalidAccessTokenError extends IOException {
	int status;
	long errorCode;
	String errorMessage;

	public InvalidAccessTokenError(int status, long errorCode, String errorMessage) {
		super();
		this.status = status;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public int getStatus() {
		return status;
	}
	
	public long getErrorCode() {
		return errorCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	private static final long serialVersionUID = 1L;
}
