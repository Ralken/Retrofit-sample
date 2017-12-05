package cn.ralken.android.http.exception;

import android.util.Pair;

import org.json.JSONException;

/**
 * Created by Ralken Liao on 02/11/2017.
 */

public class ErrorParser {

    private static final Long UNKNOWN_ERROR_CODE = -1L;

    private ErrorParser() {
    }

    public static Pair<Long, String> parseUnknownException(Throwable e) {
        return new Pair<>(UNKNOWN_ERROR_CODE, e.getMessage());
    }

    public static Pair<Long, String> parseBaseHttpException(BaseHttpException exception) {
        return exception.toErrorCodePair();
    }
}
