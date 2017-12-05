package cn.ralken.android.http.exception;

/**
 * Created by Ralken Liao on 02/11/2017.
 */

public class NoNetWorkException extends BaseHttpException {

    public NoNetWorkException() {
        super(1001L, "网络未连接");
    }
}
