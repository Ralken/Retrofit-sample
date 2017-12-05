package cn.ralken.android.http;

import android.util.Pair;

import cn.ralken.android.http.exception.BaseHttpException;
import cn.ralken.android.http.exception.ErrorParser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * A default {@link Observer} implementation that handles callback from Retrofit request.
 *
 * @author liaoralken
 */

public abstract class HttpObserver<T> implements Observer<T> {

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

    @Override
    public void onComplete() {
    }
}
