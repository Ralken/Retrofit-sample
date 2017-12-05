package cn.ralken.android.http.exception;

/**
 * Created by Ralken Liao on 02/11/2017.
 */

public class UnExpectedHttpStatusException extends BaseHttpException {

    public UnExpectedHttpStatusException(int code) {
        super(code, "异常HTTP STATUS CODE:" + code);
    }

}
