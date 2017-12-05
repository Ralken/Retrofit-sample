package cn.ralken.android.http;

import android.util.Pair;

import cn.ralken.android.http.exception.BaseHttpException;
import cn.ralken.android.http.exception.ErrorParser;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

/**
 * Created by Ralken Liao on 16/11/2017.
 */

public abstract class HttpSingleObserver<T> implements SingleObserver<T>{

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public final void onError(Throwable e) {
        final Class clz = e.getClass();
        final Pair<Long, String> errorPair;

        if (e instanceof BaseHttpException) {
            BaseHttpException exception = (BaseHttpException) e;
            errorPair = ErrorParser.parseBaseHttpException(exception);
        } else {
            errorPair = ErrorParser.parseUnknownException(e);
        }

        // We don't have to make null-check for #errorPair here.
        onError(errorPair.first, errorPair.second);
    }

    protected abstract void onError(long code, String message);

}
