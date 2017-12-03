package com.henong.android.http.exception;

import java.io.IOException;

public class IllegalStatusError extends IOException {
    private static final long serialVersionUID = 1L;

    private long errorCode;

    private String errorMessage;

    public IllegalStatusError(long errorCode, String errorMessage) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public long getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
