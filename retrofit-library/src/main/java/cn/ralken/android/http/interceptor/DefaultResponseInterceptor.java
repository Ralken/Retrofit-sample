package cn.ralken.android.http.interceptor;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

import cn.ralken.android.http.exception.IllegalStatusError;
import cn.ralken.android.http.exception.InvalidAccessTokenError;
import cn.ralken.android.http.exception.InvalidJsonFormatException;
import cn.ralken.android.http.exception.NotJsonException;
import cn.ralken.android.http.exception.UnExpectedHttpStatusException;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Ralken Liao on 01/11/2017.
 */

public class DefaultResponseInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Response response = chain.proceed(chain.request());

        if (!response.isSuccessful()) {  // Http status code: Erroneous (status 400-599)
            deliverUnexpectedStatusCodeError(response.code());
        }

        /**
         *  NOTE: <br>
         *  the Response stream will be closed when following methods been called: <br>
         *  {@link ResponseBody#string()}
         *  {@link ResponseBody#source()}
         *  {@link ResponseBody#bytes()}
         *
         *  in this case the response data should not be used any more.
         */
        final String responseBody = response.body().string();

        try {
            final Object format = new JSONTokener(responseBody).nextValue();

            if (format instanceof JSONObject) {
                final JSONObject jsonObject = new JSONObject(responseBody);
                final Integer status = jsonObject.optInt("status", -2);
                final long errorCode = jsonObject.optLong("errorCode", -2);
                final String errorMessage = jsonObject.optString("errorMessage");
                final String result = jsonObject.optString("result");

                // compat older apis.
                final String erroeMsg = jsonObject.optString("erroeMsg");
                final String errorMsg = jsonObject.optString("errorMsg");

                final boolean isNewDataStructure = (status != -2) && (!TextUtils.isEmpty(result) || errorCode != -2);
                deliverInvalidJsonFormatException(isNewDataStructure);

                boolean hasAccessTokenError = checkIfAccessTokenError(status, errorCode);
                if (hasAccessTokenError) {    // Check login token error.
                    throw new InvalidAccessTokenError(status, errorCode, errorMessage);
                } else if (erroeMsg != null && !"".equals(erroeMsg)) {
                    /** Logic business error code. */
                    throw new IllegalStatusError(errorCode, erroeMsg);
                } else if (errorMsg != null && !"".equals(errorMsg)) {
                    /** Logic business error code. */
                    throw new IllegalStatusError(errorCode, errorMsg);
                }

                ResponseBody newBody = ResponseBody.create(response.body().contentType(), result);
                return response.newBuilder().body(newBody).build();
            }

            throw new NotJsonException();
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    /* Check error condition when retrieving access token from backend. */
    private boolean checkIfAccessTokenError(Integer status, long errorCode) {
        return false;
    }

    private static void deliverInvalidJsonFormatException(boolean isNewDataStructure) throws InvalidJsonFormatException {
        if (!isNewDataStructure) {
            throw new InvalidJsonFormatException();
        }
    }

    private static void deliverUnexpectedStatusCodeError(int code) throws UnExpectedHttpStatusException {
        throw new UnExpectedHttpStatusException(code);
    }

}
