package cn.ralken.android.http.exception;

import android.util.Pair;

import java.io.IOException;

/**
 * Created by Ralken Liao on 02/11/2017.
 */

public abstract class BaseHttpException extends IOException {
    private long code;

    public BaseHttpException() {
    }

    public BaseHttpException(long code, String message) {
        super(message);
        this.code = code;
    }

    public Pair<Long, String> toErrorCodePair() {
        return new Pair<>(code, getMessage());
    }

}
