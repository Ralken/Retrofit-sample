package cn.ralken.android.http.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Do NOT delete this class even it's recently unused.
 *
 * Created by Ralken Liao on 02/11/2017.
 */

public class ResponseResultConverterFactory extends Converter.Factory {

    private GsonConverterFactory factory;

    public ResponseResultConverterFactory(GsonConverterFactory factory) {
        this.factory = factory;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(final Type type, Annotation[] annotations, Retrofit retrofit) {
        Type wrappedType = new ParameterizedType() {
            @Override public Type[] getActualTypeArguments() {
                return new Type[]{type};
            }

            @Override public Type getOwnerType() {
                return null;
            }

            @Override public Type getRawType() {
                return GenericResponseEntity.class;
            }
        };
        Converter<ResponseBody, ?> gsonConverter = factory
                .responseBodyConverter(wrappedType, annotations, retrofit);
        return new ResponseSubResultBodyConverter(gsonConverter);
    }
}