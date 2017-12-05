package cn.ralken.android.http;

import android.content.Context;

import com.google.gson.Gson;
import cn.ralken.android.http.interceptor.DefaultRequestInterceptor;
import cn.ralken.android.http.interceptor.DefaultResponseInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ralken Liao on 31/10/2017.
 */

public class RemoteServiceFactory {

    public static <T extends Interceptor> T create(Context context, Class<? extends T> clz) {
        return create(context, clz, new Interceptor[]{null});
    }

    public static <T extends Interceptor> T create(Context context, Class<? extends T> clz, Interceptor... interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        /** Set up interceptor(s) strategy for OkHttpClient. */
        if (null == interceptors || interceptors[0] == null) {
            builder.addInterceptor(obtainDefaultResponseInterceptor());
            builder.addInterceptor(obtainDefaultRequestInterceptor(context));
            builder.addInterceptor(obtainDefaultLogcatInterceptor());

        } else {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }

        OkHttpClient okHttpClient = builder.build();

        /** Inject a Gson instance which escapes html tag. */
        final Gson gson = new Gson();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBasicGatewayUrl())
                .client(okHttpClient)
                //.addConverterFactory(new ResponseResultConverterFactory(GsonConverterFactory.create(gson)))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(clz);
    }

    private static Interceptor obtainDefaultRequestInterceptor(Context context) {
        return new DefaultRequestInterceptor(context);
    }

    private static Interceptor obtainDefaultResponseInterceptor() {
        return new DefaultResponseInterceptor();
    }

    private static Interceptor obtainDefaultLogcatInterceptor() {
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    private static String getBasicGatewayUrl() {
        return "https://api.github.com";
    }

}
