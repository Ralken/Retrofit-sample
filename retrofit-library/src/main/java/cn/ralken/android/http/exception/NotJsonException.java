package cn.ralken.android.http.exception;

/**
 * Created by Ralken Liao on 02/11/2017.
 */

public class NotJsonException extends BaseHttpException {

    public NotJsonException() {
        super(1003L, "服务端返回非Json格式");
    }
}
