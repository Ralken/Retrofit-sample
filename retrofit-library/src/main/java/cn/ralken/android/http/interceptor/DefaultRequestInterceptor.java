package cn.ralken.android.http.interceptor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import cn.ralken.android.http.exception.NoNetWorkException;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Ralken Liao on 31/10/2017.
 */
public class DefaultRequestInterceptor implements Interceptor {
    private static Gson mGson = new Gson();

    /**
     * Content type for all the requests.
     */
    private static final String PROTOCOL_CONTENT_TYPE = "application/x-www-form-urlencoded";

    public static final String PROTOCOL_PARAM_SERVERNAME = "serverName";
    public static final String PROTOCOL_PARAM_INPUTPARAM = "inputParam";

    private Context context;

    public DefaultRequestInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        assertRequestPreconditions(context);

        final Request originRequest = chain.request();

        final Request.Builder newRequestBuilder = originRequest.newBuilder();

        /** Body 1: Build each of the fields as map in order to make json format. */
        FormBody requestBody = (FormBody) originRequest.body();
        int paramCount = requestBody.size();

        Map<String, Object> map = new HashMap<>();
        int paramsCount = requestBody.size();
        for (int i = 0; i < paramCount; i++) {
            map.put(requestBody.name(i), requestBody.value(i));
        }
        String inputParamJsonBody = mGson.toJson(map);

        /** Body 2: Build out layer request params with method POST. */
        HttpUrl httpUrl = originRequest.url();
        String[] splitUrls = splitFullServerName(httpUrl);

        FormBody formBody = new FormBody.Builder()
                .add(PROTOCOL_PARAM_SERVERNAME, splitUrls[1])
                .add(PROTOCOL_PARAM_INPUTPARAM, inputParamJsonBody)
                .build();

        newRequestBuilder.method("POST", formBody);

        /** Url: Add query parameter for every request. */
        try {
            Class<?> clz = httpUrl.getClass();
            Field field = clz.getDeclaredField("url");
            field.setAccessible(true);
            field.set(httpUrl, splitUrls[0]);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        /* Headers: Append user login token for all the http request header. */
        newRequestBuilder.addHeader("Content-Type", PROTOCOL_CONTENT_TYPE);

        /* Add more custom headers here */
        String accessToken = null;
        if (!TextUtils.isEmpty(accessToken)) {
            newRequestBuilder.addHeader("Access-Token", accessToken);
        }

        return chain.proceed(newRequestBuilder.build());
    }

    private static void assertRequestPreconditions(Context context) throws IOException {
        if (isNetworkAvailable(context)) {
            throw new NoNetWorkException();
        }
    }

    private String parserInputParamAsJson(Request request) {
        return null;
    }

    /**
     * Split the serverName from the original request url. egg: <br>
     * Input: http://www.google.com/sample/path/listApis <br>
     * <p>
     * Output: <br>
     * 0 ->[http://www.google.com/sample/path/] <br>
     * 1 -> [listApis] <br>
     *
     * @param httpUrl
     * @return
     */
    private String[] splitFullServerName(HttpUrl httpUrl) {
        String url = httpUrl.toString();
        final int lastSeparatorIndex = url.lastIndexOf("/") + 1;
        return new String[]{url.substring(0, lastSeparatorIndex), url.substring(lastSeparatorIndex)};
    }

    /**
     * Check if there's available network.
     *
     * @param context
     * @return
     */
    private static boolean isNetworkAvailable(Context context) {
        boolean flag = false;
        ConnectivityManager cwjManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager != null && cwjManager.getActiveNetworkInfo() != null) {
            try {
                flag = cwjManager.getActiveNetworkInfo().isAvailable();
            } catch (Exception e) {
                e.printStackTrace();
                return flag;
            }
        }
        return flag;
    }
}