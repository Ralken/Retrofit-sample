package cn.ralken.android.http.exception;

/**
 * Created by Ralken Liao on 02/11/2017.
 */

public class InvalidJsonFormatException extends BaseHttpException {

    public InvalidJsonFormatException() {
        super(1002L, "返回Json不符合规则");
    }
}
